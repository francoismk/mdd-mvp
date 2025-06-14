import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Topic } from "../../../core/models";

@Injectable({ providedIn: "root" })
export class TopicService {
  private http = inject(HttpClient);
  private baseUrl = "http://localhost:8080/api/topics";


  getTopics(): Observable<Topic[]> {
    return this.http.get<Topic[]>(this.baseUrl, { withCredentials: true });
  }
}
