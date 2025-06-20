import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import type { User, UserRequestUpdate } from "../../../core/models";

@Injectable({ providedIn: "root" })
export class UserService {
  private http = inject(HttpClient);
  private baseUrl = "http://localhost:8080/api/users";

  getUser() {
    return this.http.get<User>(`${this.baseUrl}/me`, { withCredentials: true });
  }

  unsubscribe(subscriptionId: string) {
    return this.http.delete(`${this.baseUrl}/${subscriptionId}/unsubscriptions`, { withCredentials: true });
  }

  updateUser(user: UserRequestUpdate) {
    return this.http.put(`${this.baseUrl}/me`, user, { withCredentials: true });
  }
}