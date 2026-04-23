export interface PatientRecordModel {
  id: string;
  patientId: string;
  patientFullName: string;
  recordCreatedByFullName: string;
  patientAge: number;
  patientGender: string;
  updateAt: string;
  createdAt: string;
  patientHistory: Record<string, string>;
}