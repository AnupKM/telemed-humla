import { Injectable, signal } from '@angular/core';
import { PatientModel } from '@shared/models/patient';

@Injectable({
  providedIn: 'root',
})
export class PatientStateService {
  selectedPatient = signal<PatientModel | null>(null);

  setPatient(patient: PatientModel) {
    this.selectedPatient.set(patient);
  }

  clearPatient() {
    this.selectedPatient.set(null);
  }
}
