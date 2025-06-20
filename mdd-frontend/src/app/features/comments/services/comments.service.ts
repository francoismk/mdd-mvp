import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import type { ArticleComment } from "../../../core/models";
import type { Observable } from "rxjs";
import type { CreateComment } from "../../../core/models/create-comment.interface";

@Injectable({ providedIn: "root" })
export class CommentService {
  private http = inject(HttpClient);
  private baseUrl = "http://localhost:8080/api/comments";

  getComments() {
    return this.http.get<Comment[]>(this.baseUrl, { withCredentials: true });
  }

  createComment(comment: CreateComment, articleId: string): Observable<ArticleComment> {
    return this.http.post<ArticleComment>(`${this.baseUrl}?articleId=${articleId}`, comment, { withCredentials: true });
  }
}