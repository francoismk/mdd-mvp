import { Component, effect, inject } from "@angular/core";
import { UserService } from "../services/user.service";
import { FormBuilder, ReactiveFormsModule } from "@angular/forms";
import { toSignal } from "@angular/core/rxjs-interop";
import type { UserRequestUpdate } from "../../../core/models";

@Component({
  selector: "app-user-profile",
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <h2>Profil utilisateur</h2>
    <form [formGroup]="userProfileForm">
      <input id="username" type="text" formControlName="username">
      <input id="email" type="email" formControlName="email">
      <input id="password" type="password" formControlName="password" placeholder="Mot de passe">
      <button type="submit" (click)="updateUser(userProfileForm.value)">Sauvegarder</button>
    </form>
    <hr>
    <h2>Abonnements</h2>
    @for (subscription of user()?.subscriptions; track subscription.id) {
      <p>{{ subscription.name }}</p>
      <button type="submit" (click)="unsubscribe(subscription.id)">Se désabonner</button>
    }
  `
})
export class UserProfileComponent {
  private userService = inject(UserService);
  private formBuilder = inject(FormBuilder);
  user = toSignal(this.userService.getUser(), { initialValue: null });

  userProfileForm = this.formBuilder.nonNullable.group({
    username: "",
    email: "",
    password: "",
  });

  constructor() {
    effect(() => {
    console.log("user profile component");
      console.log("user profile: ", this.user());
      this.userProfileForm.patchValue({
        username: this.user()?.username,
        email: this.user()?.email,
      });
    });
  }

  unsubscribe(subscriptionId: string) {
    this.userService.unsubscribe(subscriptionId).subscribe({
      next: () => {
          const user = this.user();
          if (user) {
              user.subscriptions = user.subscriptions.filter(subscription => subscription.id !== subscriptionId);
              console.log("Unsubscribed from topic");
            }
      }
    });
  }

  updateUser(user: UserRequestUpdate) {
    this.userService.updateUser(user).subscribe({
      next: () => {
        console.log("User updated");
      }
    });
  }
}