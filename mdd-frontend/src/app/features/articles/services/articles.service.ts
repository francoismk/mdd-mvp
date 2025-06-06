import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Article } from "../../../core/models";

// Récupération des articles via httpClient ici ?
@Injectable({ providedIn: "root" })
export class ArticleService {
  private http = inject(HttpClient);
  private baseUrl = "http://localhost:8080/api/articles?sort=date_asc";

  getArticles(): Observable<Article[]> {
    const token =
      "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiamFuZS5zbWl0aEBtYWlsLmNvbSIsImV4cCI6MTc0OTMwMzkxNiwiaWF0IjoxNzQ5MjE3NTE2fQ.7jDhT5NOOvigrKPD1gpiOglm_xK1ExCXBXFFs4AUTzY";

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    });

    return this.http.get<Article[]>(this.baseUrl, { headers });
  }
}
