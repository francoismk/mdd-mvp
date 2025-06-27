import { Component, computed, effect, inject } from "@angular/core";
import { ArticleService } from "../services/articles.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute } from "@angular/router";
import { of } from "rxjs";
import type { ArticleComment } from "../../../core/models";
import { FormBuilder, ReactiveFormsModule } from "@angular/forms";
import { CommentService } from "../../comments/services/comments.service";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

@Component({
  selector: "app-article-by-id",
  standalone: true,
  template: `
    <div class="article-container">
      <div class="article-header">
        <a routerLink="/articles" class="back-button">
          <img src="assets/images/back-icon.svg" alt="Arrow Left" class="back-icon">
        </a>
        <h1 class="article-title">{{ article()?.title }}</h1>
      </div>

      <div class="article-main">
        <div class="article-meta">
          <span class="meta-item">{{ article()?.createdAt }}</span>
          <span class="meta-item">{{ article()?.author?.username }}</span>
          <span class="meta-item">{{ article()?.topic?.name }}</span>
        </div>

        <div class="article-content">
          <p>{{ article()?.content }}</p>
        </div>

        <hr class="divider">

        <div class="comments-section">
          <p class="section-title">Commentaires</p>

          <div class="comments-list">
          @for (articleComment of articleComments(); track $index) {
            <div class="comment-container">
              <div class="comment-author">
                {{ articleComment.author.username }}
              </div>
              <div class="comment-card">
              <p class="comment-content">{{ articleComment.content }}</p>
              </div>
            </div>
          }
          </div>
          <form [formGroup]="createCommentForm" (ngSubmit)="onSubmit()" class="comment-form">
            <div class="form-group">
              <textarea id="content" formControlName="content" placeholder="Écrivez votre commentaire" class="comment-input"></textarea>
              <button type="submit" class="send-button">
                <img src="assets/images/send-icon.svg" alt="Send">
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: `
    .article-container {
      max-width: 100%;
      margin: 0 auto;
      padding: 2rem 1.5rem;
    }

    .article-header {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .back-icon {
      width: 24px;
      height: 24px;
    }

    .article-title {
      font-size: 1.75rem;
      margin: 0;
      color: #333;
    }

    .article-main {
      padding-left: calc(24px + 1rem);
    }

    .article-meta {
      display: flex;
      gap: 1.25rem;
      color: #666;
      margin-bottom: 2rem;
      align-items: center;
    }

    .meta-separator {
      color: #999;
    }

    .article-content {
      font-size: 1.1rem;
      line-height: 1.6;
      color: #333;
      margin-bottom: 2rem;
    }

    .divider {
      border: none;
      border-top: 1px solid #eee;
      margin: 2rem 0;
    }

    .section-title {
      font-size: 1.5rem;
      margin-bottom: 1.5rem;
      color: #333;
    }

    .comments-section {
      max-width: 600px;
    }

    .comments-list {
      margin-left: 2rem;
      margin-bottom: 2rem;
      max-width: 500px;
    }

    .comment-container {
      display: flex;
      gap: 1rem;
      margin-bottom: 1rem;
      align-items: flex-start;
      max-width: 100%;
    }

    .comment-author {
      flex: 0 0 80px;
      word-wrap: break-word;
      font-size: 0.9rem;
    }

    .comment-card {
      background-color: #f5f5f5;
      border-radius: 8px;
      padding: 1rem;
      flex: 1;
      max-width: 400px;
    }

    .comment-header {
      flex: 0 0 50px;
      font-weight: 600;
      color: #333;
    }

    .comment-content {
      flex: 1;
      color: #444;
      margin: 0;
    }

    .comment-form {
      margin-top: 2rem;
      margin-left: 2rem;
      max-width: 500px;
    }

    .form-group {
      display: flex;
      gap: 1rem;
      align-items: center;
    }

    .comment-input {
      flex: 1;
      min-width: 0;
      min-height: 44px;
      padding: 0.75rem 1rem;
      border: 1px solid #ddd;
      border-radius: 8px;
      font-size: 1rem;
      resize: vertical;
    }

    .send-button {
      background: none;
      border: none;
      border-radius: 8px;
      padding: 0.5rem;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: background-color 0.2s;
    }

    .send-icon {
      width: 20px;
      height: 20px;
    }

    @media (max-width: 768px) {
      .article-main {
        padding-left: 0;
      }

      .article-header {
        align-items: flex-start;
      }

      .article-title {
        font-size: 1.5rem;
      }

      .article-meta {
        flex-wrap: wrap;
        gap: 0.5rem 1.25rem;
      }

      .meta-item {
        white-space: nowrap;
      }

      .comments-section {
        max-width: 100%;
      }

      .comments-list {
        margin-left: 0;
      }

      .comment-form {
        margin-left: 0;
      }

      .comment-container {
        flex-direction: column;
        align-items: flex-end;
        gap: 0.5rem;
      }

      .comment-author {
        flex-basis: auto;
        width: auto;
      }

      .comment-card {
        width: 100%;
      }
    }
    `,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterModule
  ]
})
export class ArticleByIdComponent {
  private articleService = inject(ArticleService);
  private route = inject(ActivatedRoute);
  private articleId = this.route.snapshot.paramMap.get("id");
  article = toSignal(
    this.articleId
      ? this.articleService.getArticlesById(this.articleId)
      : of(null),
    { initialValue: null },
  );
  readonly articleComments = computed(
    (): ArticleComment[] => this.article()?.comments ?? [],
  );
  private formBuilder = inject(FormBuilder);
  private commentService = inject(CommentService);

  createCommentForm = this.formBuilder.nonNullable.group({
    content: "",
  });

  onSubmit() {
    console.log("test de l'envoi du formulaire");
    console.log(this.createCommentForm.value);
    if (this.createCommentForm.valid && this.articleId) {
      const payload = this.createCommentForm.getRawValue();
      this.commentService.createComment(payload, this.articleId).subscribe({
        next: (response) => {
          console.log('Comment created successfully:', response);
          const article = this.article();
          if (article?.comments) {
            article.comments.push(response);
          }
          this.createCommentForm.reset();
        },
        error: (error) => {
          console.error('Error creating comment:', error);
        }
      });
    }
    else {
      console.error('Form is invalid:', this.createCommentForm.errors);
    }
  }

  constructor() {
    effect(() => {
      console.log("reception de l'article suivant : ", this.article());
      console.log("reception de l'id ????", this.articleId);
      console.log("petit test des commentaires", this.article()?.comments);
      console.log("test de computed", this.articleComments());
    });
  }
}
