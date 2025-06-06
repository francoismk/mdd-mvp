import { Topic } from ".";

export interface User {
  id: string;
  username: string;
  email: string;
  subscriptions: Topic[];
}
