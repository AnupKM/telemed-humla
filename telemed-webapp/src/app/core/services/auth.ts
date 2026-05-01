import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { API_ENDPOINTS } from '@core/constants/api-endpoints';
import { environment } from '@env/environment';
import { LoginModel } from '@shared/models/login';
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private readonly ACCESS_TOKEN_KEY = 'telemed_auth_token';
  private readonly USER_KEY = 'telemed_current_user';


  private _accessToken = signal<string | null>(
    localStorage.getItem(this.ACCESS_TOKEN_KEY)
  );
  readonly accessToken = this._accessToken.asReadonly();

  private _currentUser = signal<LoginModel | null>(
    JSON.parse(localStorage.getItem(this.USER_KEY) || 'null')
  );
  readonly currentUser = this._currentUser.asReadonly();

  login(credentials: any) {
    this.clearLocalSession();
    return this.http.post<LoginModel>(API_ENDPOINTS.AUTH.LOGIN, credentials).pipe(
      tap(response => {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, response.accessToken);
        localStorage.setItem(this.USER_KEY, JSON.stringify(response));

        this._accessToken.set(response.accessToken);
        this._currentUser.set(response);
      })
    );
  }

  refresh() {
    return this.http.post<LoginModel>(`${environment.apiUrl}/api/auth/refresh`, {}, { withCredentials: true }).pipe(
      tap(response => {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, response.accessToken);
        localStorage.setItem(this.USER_KEY, JSON.stringify(response));

        this._accessToken.set(response.accessToken);
        this._currentUser.set(response);
      })
    );
  }

  getToken(): string | null {
    return this._accessToken();
  }

  getCurrentUser(): LoginModel | null {
    return this._currentUser();
  }

  isAuthenticated(): boolean {
    return !!this._accessToken();
  }

  logout() {
    return this.http.post(`${environment.apiUrl}/api/auth/logout`, {}, { withCredentials: true }).pipe(
      tap(() => this.clearLocalSession())
    );
  }

  clearLocalSession() {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this._accessToken.set(null);
    this._currentUser.set(null);
  }
}
