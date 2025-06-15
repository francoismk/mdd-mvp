import { Component, effect, inject } from "@angular/core";
import { ArticleService } from "../services/articles.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { Article } from "../../../core/models";
import { Router } from "@angular/router";

@Component({
  selector: "app-articles-pages",
  standalone: true,
  template: `
    <div>
      <h1>Articles</h1>
      <p>Check console for data</p>
      @for (article of articles(); track article.id) {
        <div (click)="handleClick(article)" style="cursor: pointer;">
          <h2>{{ article.title }}</h2>
          <p>{{ article.createdAt }}</p>
          <p>{{ article.author.username }}</p>
          <p>{{ article.content }}</p>
        </div>
      }
    </div>
  `,
})
export class ArticleListComponent {
  private articleService = inject(ArticleService);
  private readonly router = inject(Router);
  articles = toSignal(this.articleService.getArticles(), { initialValue: [] });

  constructor() {
    effect(() => {
      console.log("articles received :", this.articles());
    });
  }

  handleClick(article: Article) {
    console.log("coucou je clique test", article);
    console.log("coucou je clique même pas sur id", article.id);
    this.router.navigate(["/article", article.id]);
  }
}
