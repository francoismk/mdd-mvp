import { Component, effect, inject } from "@angular/core";
import { ArticleService } from "../services/articles.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { Article } from "../../../core/models";
import { Router } from "@angular/router";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

@Component({
  selector: "app-articles-pages",
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="articles-container">
      <div class="articles-header">
      <a routerLink="/create-article" class="create-button">Créer un article</a>
      <div class="filter-container">
      <p>Trier par</p>
      <img src="assets/images/down-arrow-icon.svg" alt="Down Arrow">
      </div>
      </div>
      <div class="articles-grid">
      @for (article of articles(); track article.id) {
        <article (click)="handleClick(article)" style="cursor: pointer;" class="article-card">
          <h2 class="article-title">{{ article.title }}</h2>
          <div class="article-meta">
          <span class="article-date">{{ article.createdAt }}</span>
          <span class="article-author">{{ article.author.username }}</span>
          </div>
          <p class="article-content">{{ article.content }}</p>
        </article>
      }
      </div>
    </div>
  `,
  styles: `
  .articles-container {
      width: 100%;
      padding: 2rem 4rem;
    }

    .articles-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .create-button {
      background-color: #7763C5;
      color: white;
      padding: 0.75rem 1.5rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
      transition: background-color 0.2s;
    }

    .create-button:hover {
      background-color: #6854b8;
    }

    .filter-container {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      cursor: pointer;
      font-weight: 600;
    }

    .filter-icon {
      width: 16px;
      height: 16px;
    }

    .articles-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 2rem;
      width: 100%;
    }

    .article-card {
      background: #F5F5F5;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition: transform 0.2s, box-shadow 0.2s;
    }

    .article-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .article-title {
      font-size: 1.25rem;
      margin: 0 0 0.5rem 0;
      color: #333;
    }

    .article-meta {
      display: flex;
      gap: 1rem;
      color: #666;
      font-size: 0.875rem;
      margin-bottom: 1rem;
    }

    .article-content {
      color: #444;
      margin: 0;
      display: -webkit-box;
      -webkit-line-clamp: 5;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    /* Responsive */
    @media (max-width: 768px) {
      .articles-grid {
        grid-template-columns: 1fr;
      }

      .articles-container {
        padding: 1rem;
      }

      .articles-header {
        flex-direction: column;
        gap: 1rem;
        align-items: flex-start;
      }
    }
  `
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
    this.router.navigate(["/article", article.id]);
  }
}
