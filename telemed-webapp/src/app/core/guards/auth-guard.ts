import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '@core/services/auth';
import { NAV_ROUTES } from '@core/constants/navigation-routes';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router);
  if (!authService.isAuthenticated()) {
    return router.parseUrl(NAV_ROUTES.LOGIN);
  }

  const user = authService.getCurrentUser();
  const requiredRole = route.data?.['role'];

  if (requiredRole && !user?.roles.includes(requiredRole)) {
    return router.parseUrl(NAV_ROUTES.UNAUTHORIZED);
  }

  return true;
};