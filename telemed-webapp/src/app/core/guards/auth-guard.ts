import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '@core/services/auth';
import { NAV_ROUTES } from '@core/constants/navigation-routes';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router);

  return authService.isAuthenticated()
    ? true
    : router.parseUrl(NAV_ROUTES.LOGIN);
};
