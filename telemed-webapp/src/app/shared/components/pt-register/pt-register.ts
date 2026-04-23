import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { PatientService as PatientService } from '@core/services/patient';
import { Router } from '@angular/router';
import { NAV_ROUTES } from '@core/constants/navigation-routes';
import { PatientModel as PatientModel } from '@shared/models/patient';

@Component({
  selector: 'app-pt-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pt-register.html',
  styleUrls: ['./pt-register.scss']
})
export class PtRegisterPage {
  private fb = inject(FormBuilder);
  private patientService = inject(PatientService);
  private router = inject(Router);

  patientForm = this.fb.group({
    firstName: ['', [Validators.required, Validators.maxLength(150)]],
    middleName: ['', [Validators.maxLength(150)]],
    lastName: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.email]],
    phone: [''],
    dateOfBirth: [null],
    age: [null, [Validators.min(0), Validators.max(130)]],
    heightCm: [null, [Validators.min(0.1)]],
    weightKg: [null, [Validators.min(0.1)]]
  });

  onSubmit() {
    if (this.patientForm.valid) {
      const payload = this.patientForm.getRawValue() as unknown as PatientModel;

      this.patientService.register(payload).subscribe({
        next: (response) => {
          this.router.navigate([NAV_ROUTES.DASHBOARD.ROOT]);
        },
        error: (err) => {
          console.error('Registration failed:', err);
        }
      });
    } else {
      this.patientForm.markAllAsTouched();
    }
  }
}