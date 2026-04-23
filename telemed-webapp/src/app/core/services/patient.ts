import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_ENDPOINTS } from '@core/constants/api-endpoints';
import { PatientModel as PatientModel } from '@shared/models/patient';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root',
})
export class PatientService {

  private http = inject(HttpClient);

  register(patientData: any) {
    return this.http.post(API_ENDPOINTS.PATIENTS.REGISTER, patientData);
  }

  getAllPatients(): Observable<PatientModel[]> {
    return this.http.get<PatientModel[]>(API_ENDPOINTS.PATIENTS.ROOT);
  }

  getPatientById(patientId: string) {
    return this.http.get<PatientModel>(
      API_ENDPOINTS.PATIENTS.GET_BY_ID(patientId)
    );
  }
}
