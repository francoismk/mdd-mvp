import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import type { Observable} from "rxjs";
import type { Topic } from "../../../core/models";

@Injectable({ providedIn: "root" })
export class TopicService {
  private http = inject(HttpClient);
  private baseUrlTopics = "http://localhost:8080/api/topics";
  private baseUrlSubscriptions = "http://localhost:8080/api/users";


  getTopics(): Observable<Topic[]> {
    return this.http.get<Topic[]>(this.baseUrlTopics, { withCredentials: true });
  }

  subscribe(topicId: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrlSubscriptions}/${topicId}/subscriptions`, {}, { withCredentials: true });
  }
}
