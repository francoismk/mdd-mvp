import { Component, inject, signal } from "@angular/core";
import { TopicService } from "../services/topics.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { UserService } from "../../user-profile/services/user.service";

@Component({
  selector: "app-topic-list",
  standalone: true,
  template: `
    <div>
      <h1>topic list</h1>
      <div>
        @for (topic of topics(); track topic.id) {
          <h2>{{ topic.name }}</h2>
          <p>{{ topic.description }}</p>
          <button 
          type="button"
          [disabled]="isSubscribed(topic.id)"
          (click)="subscribe(topic.id)">
          {{ isSubscribed(topic.id) ? "Déjà abonné" : "S'abonner" }}
        </button>
        }
      </div>
    </div>
  `,
})
export class TopicListComponent {
  private topicService = inject(TopicService);
  private userService = inject(UserService);
  topics = toSignal(this.topicService.getTopics(), {
    initialValue: [],
  });
  user = toSignal(this.userService.getUser(), {
    initialValue: null,
  });

  subcribedTopics = signal<string[]>([]);

  subscribe(topicId: string) {
    this.topicService.subscribe(topicId).subscribe({
      next: () => {
        console.log("Subscribed to topic");
        this.subcribedTopics.update(topics => [...topics, topicId]);
      }
    });
  }

  isSubscribed(topicId: string): boolean {
    if(this.subcribedTopics().includes(topicId)) {
      return true;
    }
    return !!this.user()?.subscriptions?.some(sub => sub.id === topicId);
  }
}
