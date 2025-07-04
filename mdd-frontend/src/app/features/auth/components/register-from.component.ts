import {
	AbstractControl,
	FormBuilder,
	ReactiveFormsModule,
	Validators,
} from "@angular/forms";
import { Component, inject, signal } from "@angular/core";

import { AuthService } from "../services/auth.service";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

@Component({
	selector: "app-register-form",
	standalone: true,
	imports: [ReactiveFormsModule, CommonModule, RouterModule],
	template: `
    <div class="auth-container">
        <div class="back-button">
        <a routerLink="/">
    <img src="assets/images/back-icon.svg" alt="Back" class="back-icon">
    </a>
    </div>
    <h1>S'inscrire</h1>
    <div class="auth-form">
        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
            <div class="form-group">
            <label for="email">Email </label>
            <input id="email" type="email" formControlName="email">
            @if (email?.errors?.['required'] && email?.touched) {
            <div class="error-message">
                L'email est obligatoire
            </div>
            }
            @if (email?.errors?.['email'] && email?.touched) {
                <div class="error-message">
                    L'email doit être valide
                </div>
            }
            </div>

            <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <input id="username" type="text" formControlName="username">
            @if (username?.errors?.['required'] && username?.touched) {
                    <div class="error-message">Le nom d'utilisateur est requis</div>
                }
                @if (username?.errors?.['minlength'] && username?.touched) {
                    <div class="error-message">Le nom d'utilisateur doit contenir au moins 3 caractères</div>
                }
            </div>
            <div class="form-group">
            <label for="password">Mot de passe</label>
            <input id="password" type="password" formControlName="password">
             @if (password?.errors?.['required'] && password?.touched) {
                    <div class="error-message">Le mot de passe est requis</div>
                }
              @if ((password?.errors?.['minlength'] || password?.errors?.['pattern']) && password?.touched) {
                  <div class="error-message">
                      Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial
                  </div>
              }

            </div>

            @if(backendError()) {
                  <div class="error-message">{{ backendError() }}</div>
                }
            <button type="submit" [disabled]="registerForm.invalid"
            [title]="registerForm.invalid ? 'Formulaire invalide' : 'Créer un compte'"
            >Créer un compte</button>
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

  .error-message {
    color: #ff4444;
    font-size: 0.875rem;
    margin-top: 0.25rem;
  }

  input.ng-invalid.ng-touched {
    border-color: #ff4444;
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
})
export class RegisterFormComponent {
	private formBuilder = inject(FormBuilder);
	private authService = inject(AuthService);

	registerForm = this.formBuilder.group({
		username: ["", [Validators.required, Validators.minLength(3)]],
		email: ["", [Validators.required, Validators.email]],
		password: [
			"",
			[Validators.required, Validators.minLength(8), this.validatePassword],
		],
	});

	backendError = signal<string | null>(null);

	onSubmit() {
		console.log("test de l'envoi du formulaire");
		console.log(this.registerForm.value);
		if (this.registerForm.valid) {
			console.log("formulaire valide");
			const { username, email, password } = this.registerForm.value;
			if (username && email && password) {
				console.log(
					"username, email, password from 2eme if",
					username,
					email,
					password,
				);
				this.authService.register({ username, email, password }).subscribe({
					next: (response) => {
						console.log(response);
					},
					error: (error) => {
						console.log("lerreur qui vient du front ? ");
						console.error(error.error.message);
						this.backendError.set(error.error.message);
					},
				});
			}
		}
	}

	private validatePassword(control: AbstractControl) {
		const value = control.value;
		if (!value) return null;

		if (value.length < 8) return { minlength: true };

		const hasNumber = /[0-9]/.test(value);
		const hasLower = /[a-z]/.test(value);
		const hasUpper = /[A-Z]/.test(value);
		const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);

		const valid = hasNumber && hasLower && hasUpper && hasSpecial;
		return valid ? null : { pattern: true };
	}

	get username() {
		return this.registerForm.get("username");
	}

	get email() {
		return this.registerForm.get("email");
	}

	get password() {
		return this.registerForm.get("password");
	}
}
