import {Component, inject} from '@angular/core';
import { ArticleService } from '../services/articles.service';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import { TopicService } from '../../topics/services/topics.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-article-form',
  standalone: true,
  template: `
    <div class="header">
      <a routerLink="/articles" class="back-button">
        <img src="/assets/images/back-icon.svg" alt="Arrow Left" class="back-icon">
      </a>
      <h1>Créer un nouvel article</h1>
    </div>
    <form [formGroup]="createArticleForm" (ngSubmit)="onSubmit()">

    <div class="form-group">
      <select id="topicId" formControlName="topicId" [class.placeholder]="!createArticleForm.get('topicId')?.value">
        <option value="" disabled selected>Sélectionnez un thème</option>
        @for (topic of topics(); track topic.id) {
          <option [value]="topic.id">{{ topic.name }}</option>
        }
      </select>
      @if (createArticleForm.get('topicId')?.errors?.['required'] && createArticleForm.get('topicId')?.touched) {
        <div class="error-message">Le thème est requis</div>
      }   
    </div>

    <div class="form-group">
      <input id="title" type="text" formControlName="title" placeholder="Titre de l'article" required>
      @if (createArticleForm.get('title')?.errors?.['required'] && createArticleForm.get('title')?.touched) {
        <div class="error-message">Le titre est requis</div>
      }
    </div>

    <div class="form-group">
      <textarea id="content" formControlName="content" placeholder="Contenu de l'article"></textarea>
      @if (createArticleForm.get('content')?.errors?.['required'] && createArticleForm.get('content')?.touched) {
        <div class="error-message">Le contenu est requis</div>
      }
    </div>

    <button type="submit" [disabled]="createArticleForm.invalid"
    [title]="createArticleForm.invalid ? 'Formulaire invalide' : 'Créer un article'"
    >Créer</button>
  </form>
  `,
  styles: [`
    :host {
      display: block;
      max-width: 600px;
      margin: 0 auto;
      padding: 2rem;
    }

    .header {
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      margin-bottom: 2rem;
    }

    .back-button {
      position: absolute;
      left: 0;
    }

    h1 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: bold;
      text-align: center;
    }

    form {
      display: flex;
      flex-direction: column;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.25rem;
    }

    label {
      font-weight: 500;
      margin-bottom: 0.5rem;
      margin-top: 1rem;
    }

    input[type="text"],
    select,
    textarea {
      border: 2px solid #7763C5;
      border-radius: 8px;
      padding: 0.75rem;
      font-size: 1rem;
      width: 280px;
      box-sizing: border-box;
      align-self: center;
      margin-bottom: 1rem;
      outline: none;
    }

    input[type="text"]:focus,
    select:focus,
    textarea:focus {
      border: 2.5px solid #7763C5;
    }

    select option[value=""] {
      color: #999;
    }

    select:invalid {
      color: #999;
    }

    select:valid {
      color: black;
    }

    input[type="text"],
    select {
      height: 50px;
    }

    textarea {
      height: 210px;
      min-height: 210px;
      resize: vertical;
    }

    button[type="submit"] {
      background-color: #7763C5;
      color: white;
      border: none;
      border-radius: 8px;
      margin-top: 2rem;
      cursor: pointer;
      font-size: 1rem;
      font-weight: bold;
      width: 140px;
      height: 40px;
      align-self: center;
    }

    button[disabled] {
      opacity: 0.6;
      cursor: not-allowed;
      background-color: #cccccc;
      border-color: #aaaaaa;
      color: #666666;
    }

    .error-message {
      color: #ff4444;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }

    @media (max-width: 768px) {
      .header {
        flex-direction: column;
        align-items: flex-start;
        margin-bottom: 1rem;
      }

      .back-button {
        position: static;
        margin-bottom: 1rem;
      }

      h1 {
        width: 100%;
      }
    }

    .placeholder {
      color: #999;
    }
  `],
  imports: [
    ReactiveFormsModule,
    RouterLink
  ]
})
export class ArticleFormComponent {

  private formBuilder = inject(FormBuilder);
  private articleService = inject(ArticleService);
  private topicService = inject(TopicService);
  topics = toSignal(this.topicService.getTopics(), { initialValue: [] });

  createArticleForm = this.formBuilder.nonNullable.group({
    title: ["", [Validators.required]],
    content: ["", [Validators.required]],
    topicId: ["", [Validators.required]],
  })

  onSubmit() {
    console.log("test de l'envoi du formulaire");
    console.log(this.createArticleForm.value);
    if (this.createArticleForm.valid) {
      const payload = this.createArticleForm.getRawValue();
      this.articleService.createArticle(payload).subscribe({
        next: (response) => {
          console.log('Article created successfully:', response);
          // Reset the form after successful submission
          this.createArticleForm.reset();
        },
        error: (error) => {
          console.error('Error creating article:', error);
        }
      });
    } else {
      console.error('Form is invalid:', this.createArticleForm.errors);
    }
  }
}
