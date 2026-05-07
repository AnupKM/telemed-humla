export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh'
  },
  PATIENTS: {
    ROOT: '/patients',
    REGISTER: '/patients/register',
    SEARCH: '/patients/search',
    GET_BY_ID: (id: string) => `/patients/${id}`,
  },
  RECORDS: {
    ROOT: '/records',
    BY_PATIENT: (patientId: string) => `/records/all/${patientId}`,
    GET_BY_ID: (id: string) => `/records/${id}`,
    ADD: '/records/add',
    UPDATE: (id: string) => `/records/${id}`,
    DELETE: (id: string) => `/records/${id}`,
    DOWNLOAD_PDF_BY_RECORDID: (recordId: string) => `/records/${recordId}/pdf`
  }
};