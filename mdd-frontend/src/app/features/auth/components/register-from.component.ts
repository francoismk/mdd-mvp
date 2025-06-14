import { Component, inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { AuthService } from "../services/auth.service";

@Component({
    selector: 'app-register-form',
    standalone: true,
    imports: [ReactiveFormsModule, CommonModule],
    template: `
        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
            <div class="form-group">
            <label for="email">Email: </label>
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
            <label for="username">Username: </label>
            <input id="username" type="text" formControlName="username">
            @if (username?.errors?.['required'] && username?.touched) {
                    <div class="error">Le nom d'utilisateur est requis</div>
                }
                @if (username?.errors?.['minlength'] && username?.touched) {
                    <div class="error">Le nom d'utilisateur doit contenir au moins 3 caractères</div>
                }
            </div>

            <div class="form-group">
            <label for="password">Password: </label>
            <input id="password" type="password" formControlName="password">
             @if (password?.errors?.['required'] && password?.touched) {
                    <div class="error">Le mot de passe est requis</div>
                }
                @if (password?.errors?.['minlength'] && password?.touched) {
                    <div class="error">Le mot de passe doit contenir au moins 8 caractères</div>
                }
            </div>

            <button type="submit" [disabled]="registerForm.invalid">Créer un compte</button>
        </form>
    `,
    
})
export class RegisterFormComponent {
    private formBuilder = inject(FormBuilder);
    private authService = inject(AuthService);

    registerForm = this.formBuilder.group({
        username: ["", [Validators.required, Validators.minLength(3)]],
        email: ["", [Validators.required, Validators.email]],
        password: ["", [Validators.required, Validators.minLength(8)]],
    })

    onSubmit() {
        console.log("test de l'envoi du formulaire");
        console.log(this.registerForm.value);
        if(this.registerForm.valid) {
            console.log("formulaire valide");
            const {username, email, password} = this.registerForm.value;
            if(username && email && password) {
                console.log("username, email, password from 2eme if", username, email, password);
                this.authService.register({username, email, password}).subscribe({
                    next: (response) => {
                        console.log(response);
                    },
                    error: (error) => {
                        console.error(error);
                    }
                })
            }
        }
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