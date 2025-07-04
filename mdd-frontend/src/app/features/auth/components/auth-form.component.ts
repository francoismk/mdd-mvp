import { Component, inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";

import { AuthService } from "../services/auth.service";
import { CommonModule } from "@angular/common";
import type { LoginRequest } from "../../../core/models";
import { RouterModule } from "@angular/router";

@Component({
	selector: "auth-login-form",
	standalone: true,
	template: `
  <div class="auth-container">
    <div class="back-button">
      <a routerLink="/">
    <img src="assets/images/back-icon.svg" alt="Back" class="back-icon">
    </a>
    </div>
    <h1>Se connecter</h1>
    <div class="auth-form">
    <form [formGroup]="authLoginForm" (ngSubmit)="onSubmit()">
      <div class="form-group">
      <label for="usernameOrEmail">Email ou nom d'utilisateur</label>
      <input id="usernameOrEmail" type="text" formControlName="usernameOrEmail">
      @if (usernameOrEmail?.errors?.['required'] && usernameOrEmail?.touched) {
                    <div class="error-message">Le nom d'utilisateur est requis</div>
                }
      </div>

      <div class="form-group">
      <label for="password">Mot de passe</label>
      <input id="password" type="password" formControlName="password">
      @if (password?.errors?.['required'] && password?.touched) {
                    <div class="error-message">Le mot de passe est requis</div>
                }
      </div>

      <button type="submit" [disabled]="authLoginForm.invalid"
      [title]="authLoginForm.invalid ? 'Formulaire invalide' : 'Se connecter'"
      >Se connecter</button>
    </form>
    </div>
  </div>
    `,
	styles: `
    .auth-container {
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      align-items: center;
      min-height: 100vh;
      padding: 0, 1.5rem;
      position: relative;
      padding-top: 30vh;
    }

    .back-button {
    position: absolute;
    top: 3rem;
    left: 3rem;
    cursor: pointer;
  }

  h1 {
    font-size: 2rem;
    margin-bottom: 2.5rem;
    font-weight: 600;
    color: #333;
  }

  .auth-form {
    width: 100%;
    max-width: 400px;
  }

  form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    width: 100%;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  label {
    font-size: 1rem;
    color: #333;
    display: block;
  }

  input {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
  }

  button[type="submit"] {
    width: 140px;
    height: 48px;
    background-color: #7763C5;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    margin: 1rem auto 0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
  }

  button[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: #cccccc;
  border-color: #aaaaaa;
  color: #666666;
}

  .error-message {
    color: #ff4444;
    font-size: 0.875rem;
    margin-top: 0.25rem;
  }

  input:focus {
    outline: none;
    border-color: #7763C5;
    box-shadow: 0 0 0 2px rgba(119, 99, 197, 0.2);
  }

  input:focus-visible {
    outline: none;
    border-color: #7763C5;
    box-shadow: 0 0 0 2px rgba(119, 99, 197, 0.2);
  }


  @media (max-width: 768px) {
    .back-button {
      top: 2rem;
      left: 1.5rem;
    }

    h1 {
      font-size: 1.75rem;
      margin-top: 3rem;
    }
  }
    `,
	imports: [ReactiveFormsModule, CommonModule, RouterModule],
})
export class AuthFormComponent {
	private formBuilder = inject(FormBuilder);
	private authService = inject(AuthService);

	authLoginForm = this.formBuilder.nonNullable.group({
		usernameOrEmail: ["", [Validators.required]],
		password: ["", [Validators.required]],
	});

	onSubmit() {
		("test de l'envoi du formulaire");
		this.authLoginForm.value;
		if (this.authLoginForm.valid) {
			const payload: LoginRequest = this.authLoginForm.getRawValue();
			this.authService.login(payload).subscribe({
				next: (response) => {
					// Reset the form after successful submission
					this.authLoginForm.reset();
				},
				error: (error) => {
					console.error("Error creating article:", error);
				},
			});
		} else {
			console.error("Form is invalid:", this.authLoginForm.errors);
		}
	}

	get usernameOrEmail() {
		return this.authLoginForm.get("usernameOrEmail");
	}

	get password() {
		return this.authLoginForm.get("password");
	}
}
