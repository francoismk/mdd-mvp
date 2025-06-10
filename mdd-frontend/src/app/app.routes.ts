import { Routes } from "@angular/router";
import { ArticleListComponent } from "./features/articles/components/article-list.component";
import { TopicListComponent } from "./features/topics/components/topic-list.component";
import {ArticleFormComponent} from './features/articles/components/article-form.component';

export const routes: Routes = [
  { path: "articles", component: ArticleListComponent },
  { path: "topics", component: TopicListComponent },
  { path: 'create-article', component: ArticleFormComponent }
];
