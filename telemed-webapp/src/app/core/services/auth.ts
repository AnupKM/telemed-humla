import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { API_ENDPOINTS } from '@core/constants/api-endpoints';
import { environment } from '@env/environment';
import { LoginModel } from '@shared/models/login';
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private readonly TOKEN_KEY = 'telemed_auth_token';
  private readonly USER_KEY = 'telemed_current_user';


  private _token = signal<string | null>(
    localStorage.getItem(this.TOKEN_KEY)
  );
  readonly token = this._token.asReadonly();

  private _currentUser = signal<LoginModel | null>(
    JSON.parse(localStorage.getItem(this.USER_KEY) || 'null')
  );
  readonly currentUser = this._currentUser.asReadonly();

  login(credentials: any) {
    localStorage.removeItem(this.TOKEN_KEY);
    this._currentUser.set(null);
    return this.http.post<LoginModel>(API_ENDPOINTS.AUTH.LOGIN, credentials).pipe(
      tap(user => {
        localStorage.setItem(this.TOKEN_KEY, user.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        this._token.set(user.token);
        this._currentUser.set(user);
      })
    );
  }

  getToken(): string | null {
    return this._token();
  }

  getCurrentUser(): LoginModel | null {
    return this._currentUser();
  }

  isAuthenticated(): boolean {
    return !!this._currentUser();
  }

  logout() {
    const url = `${environment.apiUrl}${API_ENDPOINTS.AUTH.LOGOUT}`;

    return this.http.post(url, {}).pipe(
      tap(() => {
        this.clearLocalSession();
      })
    );
  }

  clearLocalSession() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    this._token.set(null);
    this._currentUser.set(null);
  }
}
