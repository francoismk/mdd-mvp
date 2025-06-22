import { Component } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="main-container">
        <img src="assets/images/logo.svg" alt="MDD Logo" class="logo-img">
        <div class="auth-buttons">
            <a routerLink="/login" class="btn">Se connecter</a>
            <a routerLink="/register" class="btn">S'inscrire</a>
        </div>
    </div>
  `,
  styles: `
    .main-container {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        height: 100vh;
        gap: 1rem;
    }
    .logo-img {
        width: 412px;
        height: 238px;
    }
    .auth-buttons {
    display: flex;
    gap: 2.75rem;
  }

  .btn {
    padding: 0.5rem 2rem;
    font-weight: 600;
    text-decoration: none;
    cursor: pointer;
    text-align: center;
    min-width: 180px;
    border: 1px solid #000000;
    border-radius: 8px;
    font-size: 16px
  }

  @media (max-width: 768px) {
    .logo-img {
        width: 225px;
        height: 130px;
        margin-bottom: 5rem;
    }
    .auth-buttons {
        flex-direction: column;
        gap: 5rem;
        width: 100%;
        max-width: 300px;
    }

    .btn {
        width: 100%;
    }
    `,
})
export class HomeComponent {
    constructor() {}
}