import { Component, computed, effect, inject } from "@angular/core";
import { ArticleService } from "../services/articles.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute } from "@angular/router";
import { of } from "rxjs";
import { ArticleComment } from "../../../core/models";

@Component({
  selector: "app-article-by-id",
  standalone: true,
  template: `
    <div>
      <h2>{{ article()?.title }}</h2>
      <p>{{ article()?.createdAt }}</p>
      <p>{{ article()?.author?.username }}</p>
      <p>{{ article()?.topic?.name }}</p>
      <p>{{ article()?.content }}</p>
    </div>
    <div>
      <h2>Commentaires</h2>
      @for (articleComment of articleComments(); track $index) {
        <p>{{ articleComment.author.username }}</p>
        <p>{{ articleComment.content }}</p>
      }
    </div>
  `,
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

  constructor() {
    effect(() => {
      console.log("reception de l'article suivant : ", this.article());
      console.log("reception de l'id ????", this.articleId);
      console.log("petit test des commentaires", this.article()?.comments);
      console.log("test de computed", this.articleComments());
    });
  }
}
