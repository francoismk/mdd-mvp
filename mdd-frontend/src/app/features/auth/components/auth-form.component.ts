import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule,} from '@angular/forms';
import {AuthService} from '../services/auth.service';
import {LoginRequest} from '../../../core/models';

@Component({
  selector: 'auth-login-form',
  standalone: true,
  template: `
    <form [formGroup]="authLoginForm" (ngSubmit)="onSubmit()">
      <label for="usernameOrEmail">Username or Email: </label>
      <input id="usernameOrEmail" type="text" formControlName="usernameOrEmail">

      <label for="password">password: </label>
      <input id="password" type="text" formControlName="password">

      <button type="submit">Se connecter</button>
    </form>
  `,
  imports: [
    ReactiveFormsModule
  ]
})
export class AuthFormComponent {

  private formBuilder = inject(FormBuilder);
  private authService = inject(AuthService);

  authLoginForm = this.formBuilder.nonNullable.group({
    usernameOrEmail: "",
    password: "",
  })

  onSubmit() {
    console.log("test de l'envoi du formulaire");
    console.log(this.authLoginForm.value);
    if (this.authLoginForm.valid) {
      const payload: LoginRequest = this.authLoginForm.getRawValue();
      this.authService.login(payload).subscribe({
        next: (response) => {
          console.log('Article created successfully:', response);
          // Reset the form after successful submission
          this.authLoginForm.reset();
        },
        error: (error) => {
          console.error('Error creating article:', error);
        }
      });
    } else {
      console.error('Form is invalid:', this.authLoginForm.errors);
    }
  }
}




