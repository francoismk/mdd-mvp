import { Injectable, inject, signal } from "@angular/core";
import { LoginRequest, LoginResponse } from "../../../core/models";
import { catchError, of, tap } from "rxjs";

import { HttpClient } from "@angular/common/http";
import type { RegisterRequest } from "../../../core/models/register-request.interface";

@Injectable({ providedIn: "root" })
export class AuthService {
	private http = inject(HttpClient);
	private baseUrl = "http://localhost:8080/api/auth";

	isLoggedIn = signal<boolean>(false);
	currentUser = signal<string | null>(null);

	login(loginRequest: LoginRequest) {
		const url = `${this.baseUrl}/login`;
		return this.http.post(url, loginRequest, { withCredentials: true }).pipe(
			tap((response) => {
				this.isLoggedIn.set(true);
				this.currentUser.set(loginRequest.usernameOrEmail);
			}),
			catchError((error) => {
				console.error("Login failed:", error);
				this.isLoggedIn.set(false);
				this.currentUser.set(null);
				throw error; // Re-throw the error for further handling if needed
			}),
		);
	}

	register(registerRequest: RegisterRequest) {
		const url = `${this.baseUrl}/register`;
		return this.http.post(url, registerRequest, { withCredentials: true }).pipe(
			tap((response) => {
				console.log(response);
			}),
			catchError((error) => {
				console.error("Registration failed:", error);
				throw error;
			}),
		);
	}

	logout() {
		const url = `${this.baseUrl}/logout`;
		return this.http.post(url, null, { withCredentials: true }).pipe(
			tap(() => {
				this.isLoggedIn.set(false);
				this.currentUser.set(null);
			}),
			catchError((error) => {
				console.error("Logout failed:", error);
				throw error;
			}),
		);
	}

	checkAuthStatus() {
		if (typeof window === "undefined") {
		}
		const url = `${this.baseUrl}/me`;
		return this.http
			.get<{ username: string }>(url, { withCredentials: true })
			.pipe(
				tap((response) => {
					this.isLoggedIn.set(true);
					this.currentUser.set(response.username);
				}),
				catchError((error) => {
					this.isLoggedIn.set(false);
					this.currentUser.set(null);
					return [];
				}),
			);
	}
}
