import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '@env/environment';
import { Auth } from '@core/services/auth';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(Auth);
  const baseUrl = environment.apiUrl;
  const token = authService.getToken();

  let url = req.url;
  if (!url.startsWith('http')) {
    url = `${baseUrl}${req.url}`;
  }

  let apiReq = req.clone({ url });

  if (token) {
    apiReq = apiReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(apiReq);
};