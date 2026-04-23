import { Component, inject, signal, effect } from '@angular/core'; import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from "@angular/router";
import { NAV_ROUTES } from '@core/constants/navigation-routes';
import { Auth } from '@core/services/auth';
import { PatientStateService } from '@core/services/patient-state';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
})
export class Dashboard {
  private auth = inject(Auth);

  user = this.auth.currentUser;
  readonly NAV = NAV_ROUTES;
  private ptState = inject(PatientStateService);
  selectedPatient = this.ptState.selectedPatient;

  showHistoryTab(): boolean {
    return this.selectedPatient() !== null;
  }

}

