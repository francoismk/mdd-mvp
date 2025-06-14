import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Topic } from "../../../core/models";

@Injectable({ providedIn: "root" })
export class TopicService {
  private http = inject(HttpClient);
  private baseUrl = "http://localhost:8080/api/topics";
  private token =
    "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiamFuZS5zbWl0aEBtYWlsLmNvbSIsImV4cCI6MTc0OTQwMTc0OCwiaWF0IjoxNzQ5MzE1MzQ4fQ.4UGOwSvDzQONmKUrtZpiJwc-XAUKQkBcBM7PYH6NAbs";

  private headers = new HttpHeaders({
    Authorization: `Bearer ${this.token}`,
    "Content-Type": "application/json",
  });

  getTopics(): Observable<Topic[]> {
    return this.http.get<Topic[]>(this.baseUrl, { headers: this.headers });
  }
}
