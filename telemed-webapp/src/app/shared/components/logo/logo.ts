import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NAV_ROUTES } from '@core/constants/navigation-routes';

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './logo.html',
  styleUrl: './logo.scss',
})
export class Logo {
  protected readonly NAV = NAV_ROUTES;
}
