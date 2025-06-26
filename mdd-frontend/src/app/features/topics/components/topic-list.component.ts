import { Component, inject, signal } from "@angular/core";
import { TopicService } from "../services/topics.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { UserService } from "../../user-profile/services/user.service";

@Component({
  selector: "app-topic-list",
  standalone: true,
  template: `
    <div class="topics-container">
      <div class="topics-grid">
        @for (topic of topics(); track topic.id) {
          <div class="topic-card">
            <h3 class="topic-title">{{ topic.name }}</h3>
            <p class="topic-description">
              Description: {{ topic.description }}
            </p>
            <button
              type="button"
              class="subscribe-button"
              [class.subscribed]="isSubscribed(topic.id)"
              [disabled]="isSubscribed(topic.id)"
              (click)="subscribe(topic.id)"
            >
              {{ isSubscribed(topic.id) ? "Déjà abonné" : "S'abonner" }}
            </button>
          </div>
        }
      </div>
    </div>
  `,
  styles: `
    .topics-container {
      width: 100%;
      padding: 2rem 4rem;
    }

    .topics-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 2rem;
      width: 100%;
    }

    .topic-card {
      display: flex;
      flex-direction: column;
      background: #f5f5f5;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition:
        transform 0.2s,
        box-shadow 0.2s;
    }

    .topic-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .topic-title {
      font-size: 1.25rem;
      margin: 0 0 0.5rem 0;
      color: #333;
    }

    .topic-description {
      color: #444;
      margin: 0;
      display: -webkit-box;
      -webkit-line-clamp: 5;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .subscribe-button {
      align-self: center;
      margin-top: 1rem;
      margin-bottom: -0.5rem;
      background-color: #7763c5;
      color: white;
      border: none;
      border-radius: 8px;
      padding: 0.5rem 2rem;
      font-size: 1rem;
      cursor: pointer;
      transition: background-color 0.2s;
    }

    .subscribe-button:hover:not(.subscribed) {
      background-color: #6854b8;
    }

    .subscribe-button.subscribed {
      background-color: #939393;
      cursor: not-allowed;
    }

    /* Responsive */
    @media (max-width: 768px) {
      .topics-grid {
        grid-template-columns: 1fr;
      }

      .topics-container {
        padding: 1rem;
      }

      .topics-header {
        flex-direction: column;
        gap: 1rem;
        align-items: flex-start;
      }

      .subscribe-button {
        align-self: center;
      }
    }
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
        this.subcribedTopics.update((topics) => [...topics, topicId]);
      },
    });
  }

  isSubscribed(topicId: string): boolean {
    if (this.subcribedTopics().includes(topicId)) {
      return true;
    }
    return !!this.user()?.subscriptions?.some((sub) => sub.id === topicId);
  }
}
