import { Component, effect, inject } from "@angular/core";
import { ArticleService } from "../services/articles.service";
import { toSignal } from "@angular/core/rxjs-interop";

@Component({
  selector: "app-articles-pages",
  standalone: true,
  template: `
    <div>
      <h1>Articles</h1>
      <p>Check console for data</p>
      <div>
        @for (article of articles(); track article.id) {
          <h2>{{ article.title }}</h2>
          <p>{{ article.content }}</p>
          <p>{{ article.author.username }}</p>
        }
      </div>
    </div>
  `,
})
export class ArticleListComponent {
  private articleService = inject(ArticleService);
  articles = toSignal(this.articleService.getArticles(), { initialValue: [] });

  constructor() {
    effect(() => {
      console.log("articles received :", this.articles());
    });
  }
}
