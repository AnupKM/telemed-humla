import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Auth } from '@core/services/auth';
import { environment } from '@env/environment';
import { catchError, switchMap, throwError } from 'rxjs';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(Auth);
  const token = authService.getToken();

  let apiReq = req.clone({
    url: req.url.startsWith('http') ? req.url : `${environment.apiUrl}${req.url}`,
    withCredentials: true
  });

  if (token) {
    apiReq = apiReq.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(apiReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/login') && !req.url.includes('/auth/refresh')) {
        return authService.refresh().pipe(
          switchMap((newResponse) => {
            const retryReq = req.clone({
              setHeaders: { Authorization: `Bearer ${newResponse.accessToken}` },
              withCredentials: true
            });
            return next(retryReq);
          }),
          catchError((refreshError) => {
            authService.clearLocalSession();
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
};