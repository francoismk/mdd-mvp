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
      <div class="article-layout">

      <div class="arrow-column">
        <a routerLink="/articles" class="back-button">
          <img src="assets/images/back-icon.svg" alt="Arrow Left">
        </a>
      </div>

      <div class="content-column">
        <h1 class="article-title">{{ article()?.title }}</h1>
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
      <h2 class="section-title">Commentaires</h2>
      
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
          <button type="submit">
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

    .article-layout {
      display: flex;
      gap: 2rem;
    }

    .arrow-column {
      flex: 0 0 50px;
    }

    .content-column {
      flex: 1;
    }

    .back-button {
      margin-right: 1rem;
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
      max-width: 80%;
    }

    .comments-list {
      margin-left: 2rem;
      margin-bottom: 2rem;
    }

    .comment-container {
      display: flex;
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .comment-author {
      width: 50px;
    }

    .comment-card {
      background-color: #f5f5f5;
      border-radius: 8px;
      padding: 1rem;
      flex: 1;
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
    }

    .form-group {
      display: flex;
      gap: 1rem;
      align-items: flex-start;
    }

    .comment-input {
      flex: 1;
      min-height: 44px;
      padding: 0.75rem 1rem;
      border: 1px solid #ddd;
      border-radius: 8px;
      font-size: 1rem;
      resize: vertical;
    }

    .send-button {
      background: #7763c5;
      border: none;
      border-radius: 8px;
      padding: 0.5rem;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: background-color 0.2s;
    }

    .send-button:hover {
      background-color: #6854b8;
    }

    .send-icon {
      width: 20px;
      height: 20px;
    }

    @media (max-width: 768px) {
      .article-container {
        padding: 1.5rem 1rem;
      }

      .article-title {
        font-size: 1.5rem;
      }

      .article-meta {
        flex-wrap: wrap;
        gap: 0.5rem 1.25rem;
      }

      .form-group {
        flex-direction: column;
      }

      .send-button {
        width: 100%;
        padding: 0.75rem;
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
