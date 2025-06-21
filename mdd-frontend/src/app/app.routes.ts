import type { Routes } from "@angular/router";
import { ArticleListComponent } from "./features/articles/components/article-list.component";
import { TopicListComponent } from "./features/topics/components/topic-list.component";
import { ArticleFormComponent } from "./features/articles/components/article-form.component";
import { AuthFormComponent } from "./features/auth/components/auth-form.component";
import { RegisterFormComponent } from "./features/auth/components/register-from.component";
import { ArticleByIdComponent } from "./features/articles/components/article-by-id.component";
import { UserProfileComponent } from "./features/user-profile/components/user-update.component";

export const routes: Routes = [
  { path: "", redirectTo: "/login", pathMatch: "full" },
  { path: "articles", component: ArticleListComponent },
  { path: "topics", component: TopicListComponent },
  { path: "create-article", component: ArticleFormComponent },
  { path: "login", component: AuthFormComponent },
  { path: "register", component: RegisterFormComponent },
  { path: "article/:id", component: ArticleByIdComponent },
  { path: "user-profile", component: UserProfileComponent },
  { path: "**", redirectTo: "/login" },
];
