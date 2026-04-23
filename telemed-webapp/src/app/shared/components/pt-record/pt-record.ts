import { Component, inject, OnInit, signal } from '@angular/core';
import { PatientService as PatientService } from '@core/services/patient';
import { PatientModel as PatientModel } from '@shared/models/patient';
import { NAV_ROUTES } from '@core/constants/navigation-routes';
import { Router, RouterLink } from '@angular/router';
import { PatientStateService } from '@core/services/patient-state';

@Component({
  selector: 'app-pt-record',
  standalone: true,
  imports: [],
  templateUrl: './pt-record.html',
  styleUrl: './pt-record.scss',
})
export class PtRecord implements OnInit {
  private patientService = inject(PatientService);
  private ptState = inject(PatientStateService);
  private router = inject(Router);

  readonly NAV = NAV_ROUTES;
  patients = signal<PatientModel[]>([]);
  isLoading = signal(false);

  ngOnInit() {
    this.onSearch();
  }

  viewPatientHistory(patient: PatientModel) {
    this.ptState.setPatient(patient);
    const url = this.NAV.DASHBOARD.HISTORY.replace(':patientId', patient.id);
    this.router.navigateByUrl(url);
  }

  onSearch() {
    this.isLoading.set(true);
    this.patientService.getAllPatients().subscribe({
      next: (data) => {
        this.patients.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
      }
    });
  }
}
