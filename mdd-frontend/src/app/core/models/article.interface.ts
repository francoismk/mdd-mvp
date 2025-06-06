import { Topic, User } from ".";

export interface ArticleInterface {
  id: string;
  title: string;
  content: string;
  createdAt: Date;
  author: User;
  topic: Topic;
  comments: Comment[];
}
