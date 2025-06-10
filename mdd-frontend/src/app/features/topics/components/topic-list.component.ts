import { Component, inject } from "@angular/core";
import { TopicService } from "../services/topics.service";
import { toSignal } from "@angular/core/rxjs-interop";

@Component({
  selector: "app-profil-page",
  standalone: true,
  template: `
    <div>
      <h1>topic list</h1>
      <div>
        @for (topic of topics(); track topic.id) {
          <h2>{{ topic.name }}</h2>
          <!-- add description to topic  -->
          <!-- <p>{{ topic.description }}</p> -->
        }
      </div>
    </div>
  `,
})
export class TopicListComponent {
  private topicService = inject(TopicService);
  topics = toSignal(this.topicService.getTopics(), {
    initialValue: [],
  });
}
