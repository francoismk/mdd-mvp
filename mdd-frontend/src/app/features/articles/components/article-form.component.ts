import {Component, computed, inject, signal} from '@angular/core';
import { ArticleService } from '../services/articles.service';
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-article-form',
  standalone: true,
  template: `
    <form [formGroup]="createArticleForm" (ngSubmit)="onSubmit()">
      <label for="title">Titre: </label>
      <input id="title" type="text" formControlName="title">

      <label for="topicId">topic id:</label>
      <input id="topicId" type="text" formControlName="topicId">

      <label for="content">Contenu: </label>
      <textarea id="content" formControlName="content"></textarea>

      <button type="submit">Créer un article</button>
    </form>
  `,
  imports: [
    ReactiveFormsModule
  ]
})
export class ArticleFormComponent {

  private formBuilder = inject(FormBuilder);
  private articleService = inject(ArticleService);

  createArticleForm = this.formBuilder.nonNullable.group({
    title: "",
    content: "",
    // temporaire, topic et author seront gérés par la suite
    topicId: "",
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




