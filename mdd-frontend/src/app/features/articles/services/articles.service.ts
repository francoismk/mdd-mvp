import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";

import { Article } from "../../../core/models";
import { CreateArticle } from "../../../core/models/create-article.interface";
import { Observable } from "rxjs";

// Récupération des articles via httpClient ici ?
@Injectable({ providedIn: "root" })
export class ArticleService {
	private http = inject(HttpClient);
	private baseUrl = "http://localhost:8080/api";

	private headers = new HttpHeaders({
		"Content-Type": "application/json",
	});

	getArticlesAsc(): Observable<Article[]> {
		const url = `${this.baseUrl}/articles?sort=date_asc`;
		return this.http.get<Article[]>(url, {
			headers: this.headers,
			withCredentials: true,
		});
	}

	getArticlesDesc(): Observable<Article[]> {
		const url = `${this.baseUrl}/articles?sort=date_desc`;
		return this.http.get<Article[]>(url, {
			headers: this.headers,
			withCredentials: true,
		});
	}

	getArticlesById(id: string): Observable<Article> {
		const url = `${this.baseUrl}/articles/${id}`;
		return this.http.get<Article>(url, {
			headers: this.headers,
			withCredentials: true,
		});
	}

	createArticle(article: CreateArticle): Observable<CreateArticle> {
		const url = `${this.baseUrl}/articles`;
		console.log("========= from front======== ", article);
		return this.http.post<CreateArticle>(url, article, {
			headers: this.headers,
			withCredentials: true,
		});
	}
}
