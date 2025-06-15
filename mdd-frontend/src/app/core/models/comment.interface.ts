import { User } from ".";

export interface ArticleComment {
  id: string;
  content: string;
  createdAt: Date;
  author: User;
}
