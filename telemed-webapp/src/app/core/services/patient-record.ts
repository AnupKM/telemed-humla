import { Injectable, inject } from '@angular/core'; import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { PatientRecordModel } from '@shared/models/patientRecord';
import { environment } from '@env/environment';
import { API_ENDPOINTS } from '@core/constants/api-endpoints';


@Injectable({
  providedIn: 'root',
})
export class PatientRecordService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/patient-history`;

  getRecordsByPatientId(patientId: string) {
    const fullUrl = `${API_ENDPOINTS.RECORDS.BY_PATIENT(patientId)}`;
    return this.http.get<PatientRecordModel[]>(
      API_ENDPOINTS.RECORDS.BY_PATIENT(patientId)
    );
  }

  getHistoryById(recordId: string) {
    return this.http.get<PatientRecordModel>(API_ENDPOINTS.RECORDS.GET_BY_ID(recordId));
  }

  addRecord(record: Partial<PatientRecordModel>) {
    return this.http.post<PatientRecordModel>(API_ENDPOINTS.RECORDS.ADD, record);
  }

  editRecord(record: Partial<PatientRecordModel>) {
    return this.http.put<PatientRecordModel>(API_ENDPOINTS.RECORDS.UPDATE(record.id!), record);
  }

  deleteRecord(recordId: string) {
    return this.http.delete<void>(API_ENDPOINTS.RECORDS.DELETE(recordId));
  }
}
