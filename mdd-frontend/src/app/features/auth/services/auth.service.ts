import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, tap} from 'rxjs';
import {LoginRequest, LoginResponse} from '../../../core/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/auth';

  isLoggedIn = signal<boolean>(false);
  currentUser = signal<string | null>(null);

  login(loginRequest: LoginRequest) {
    const url = `${this.baseUrl}/login`;
    return this.http.post(url, loginRequest, { withCredentials: true }).pipe(
      tap(response => {
        this.isLoggedIn.set(true);
        this.currentUser.set(loginRequest.usernameOrEmail);
        console.log(response);
        console.log('User logged in:', this.currentUser());
      }),
      catchError(error => {
        console.error('Login failed:', error);
        this.isLoggedIn.set(false);
        this.currentUser.set(null);
        throw error; // Re-throw the error for further handling if needed
      })
    );
  }

  register(username: string, email: string, password: string) {
    const url = `${this.baseUrl}/register`;
    const body = { username, email, password };
    return this.http.post(url, body);
  }
}
