export interface LoginModel {
    accessToken: string;
    refreshToken?: string;
    userId: string;
    email: string;
    fullName: string;
    roles: string[];
}