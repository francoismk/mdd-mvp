import { User } from ".";

export interface Comment {
  id: string;
  content: string;
  createdAt: Date;
  author: User;
}
