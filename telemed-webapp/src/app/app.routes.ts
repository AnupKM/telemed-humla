import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { NAV_ROUTES } from '@core/constants/navigation-routes';

const cleanPath = (path: string) => path.startsWith('/') ? path.substring(1) : path;

export const routes: Routes = [
  { path: '', redirectTo: cleanPath(NAV_ROUTES.LOGIN), pathMatch: 'full' },
  {
    path: cleanPath(NAV_ROUTES.LOGIN),
    loadComponent: () =>
      import('@pages/login/login').then(m => m.Login)
  },
  {
    path: '',
    loadComponent: () =>
      import('@layouts/main-layout/main-layout').then(m => m.MainLayout),
    canActivate: [authGuard],
    children: [
      {
        path: cleanPath(NAV_ROUTES.DASHBOARD.ROOT),
        loadComponent: () =>
          import('@pages/dashboard/dashboard').then(m => m.Dashboard),
        children: [
          {
            path: '',
            redirectTo: cleanPath(NAV_ROUTES.DASHBOARD.REGISTER).split('/').pop()!,
            pathMatch: 'full'
          },
          {
            path: cleanPath(NAV_ROUTES.DASHBOARD.REGISTER).split('/').pop(),
            loadComponent: () =>
              import('@shared/components/pt-register/pt-register').then(m => m.PtRegisterPage)
          },
          {
            path: cleanPath(NAV_ROUTES.DASHBOARD.RECORD).split('/').pop(),
            loadComponent: () =>
              import('@shared/components/pt-record/pt-record').then(m => m.PtRecord)
          },
          {
            path: cleanPath(NAV_ROUTES.DASHBOARD.HISTORY).replace('dashboard/', ''),
            loadComponent: () =>
              import('@shared/components/pt-history/pt-history').then(m => m.PtHistory)
          },
        ]
      },

      { path: '', redirectTo: cleanPath(NAV_ROUTES.DASHBOARD.ROOT), pathMatch: 'full' }]
  },
  { path: '**', redirectTo: cleanPath(NAV_ROUTES.DASHBOARD.ROOT) }
];