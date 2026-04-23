import { Component, inject } from '@angular/core';
import { Logo } from "../logo/logo";
import { NAV_ROUTES } from '@core/constants/navigation-routes';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Auth } from '@core/services/auth';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [Logo, RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {

  private authService = inject(Auth);
  private router = inject(Router);

  protected readonly NAV = NAV_ROUTES;

  onLogout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate([this.NAV.LOGIN]);
      },
      error: (err) => {
        console.error('Backend logout failed, clearing local session anyway', err);
        this.authService.clearLocalSession();
        this.router.navigate([this.NAV.LOGIN]);
      }
    });
  }
}
