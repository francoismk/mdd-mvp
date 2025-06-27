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
    <div class="profile-container">
      <div class="profile-header">
        <h2>Profil utilisateur</h2>
      </div>
      <form [formGroup]="userProfileForm" class="profile-form">
        <input id="username" type="text" formControlName="username" placeholder="Nom d'utilisateur">
        <input id="email" type="email" formControlName="email" placeholder="Email">
        <input id="password" type="password" formControlName="password" placeholder="Mot de passe">
        <button type="submit" (click)="updateUser(userProfileForm.value)">Sauvegarder</button>
      </form>
      <hr>
      <h2>Abonnements</h2>
      <div class="subscriptions-container">
        <div class="subscriptions-grid">
          @for (subscription of user()?.subscriptions; track subscription.id) {
            <div class="subscription-card">
              <h3 class="subscription-title">{{ subscription.name }}</h3>
              <p class="subscription-description">{{ subscription.description }}</p>
              <button type="button" class="unsubscribe-button" (click)="unsubscribe(subscription.id)">Se désabonner</button>
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styles: [`
    .profile-container {
      width: 100%;
      padding: 2rem 4rem;
      margin: 0;
    }
    .profile-header {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 2rem;
    }
    .profile-header h2 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: bold;
      text-align: center;
    }
    .profile-form {
      display: flex;
      flex-direction: column;
      margin-bottom: 2rem;
    }
    .profile-form input[type="text"],
    .profile-form input[type="email"],
    .profile-form input[type="password"] {
      border: 2px solid #7763C5;
      border-radius: 8px;
      padding: 0.75rem;
      font-size: 1rem;
      width: 280px;
      box-sizing: border-box;
      align-self: center;
      margin-bottom: 1rem;
      outline: none;
    }
    .profile-form input:focus {
      border: 2.5px solid #7763C5;
    }
    .profile-form button[type="submit"] {
      background-color: #7763C5;
      color: white;
      border: none;
      border-radius: 8px;
      margin-top: 2rem;
      cursor: pointer;
      font-size: 1rem;
      font-weight: bold;
      width: 140px;
      height: 40px;
      align-self: center;
    }
    .subscriptions-container {
      width: 100%;
      padding: 0;
    }
    .subscriptions-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 2rem;
      width: 100%;
    }
    .subscription-card {
      display: flex;
      flex-direction: column;
      background: #f5f5f5;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      transition: transform 0.2s, box-shadow 0.2s;
      min-width: 0;
      width: 100%;
      box-sizing: border-box;
      cursor: pointer;
    }
    .subscription-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    .subscription-title {
      font-size: 1.25rem;
      margin: 0 0 0.5rem 0;
      color: #333;
    }
    .subscription-description {
      color: #444;
      margin: 0 0 1rem 0;
      display: -webkit-box;
      -webkit-line-clamp: 5;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .unsubscribe-button {
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
    .unsubscribe-button:hover {
      background-color: #6854b8;
    }
    @media (max-width: 768px) {
      .profile-container {
        padding: 1rem;
      }
      .profile-header {
        margin-bottom: 1rem;
      }
      .subscriptions-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
      }
      .profile-form input[type="text"],
      .profile-form input[type="email"],
      .profile-form input[type="password"] {
        width: 100%;
      }
    }
  `]
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