export interface PatientModel {
  id: string;
  firstName: string;
  middleName?: string;
  gender: string;
  lastName: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  age: number;
  heightCm: number;
  weightKg: number;
}