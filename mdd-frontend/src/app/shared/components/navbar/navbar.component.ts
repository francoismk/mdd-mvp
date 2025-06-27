import { Component, inject } from "@angular/core";
import { Router, RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { AuthService } from "../../../features/auth/services/auth.service";

@Component({
	selector: "app-navbar",
	standalone: true,
	imports: [CommonModule, RouterModule],
	template: `
  @if (authService.isLoggedIn()) {
    <nav class="navbar">
      <div class="logo">
        <a routerLink="/"><img src="assets/images/logo.svg" alt="MDD Logo" class="logo-img"></a>
      </div>

      <button class="burger-button" (click)="toggleMenu()" aria-label="Menu">
    <img src="assets/images/menu-burger.svg" alt="Menu" class="burger-icon">
  </button>

      <div class="nav-links">
        <a (click)="logout()" class="deconnected-link" style="cursor: pointer;">Se déconnecter</a>
        <a routerLink="/articles" routerLinkActive="active">Articles</a>
        <a routerLink="/topics" routerLinkActive="active">Thèmes</a>
        <a routerLink="/user-profile" routerLinkActive="active" #profileLink="routerLinkActive">
        <img [src]="profileLink.isActive ? 'assets/images/profil-active.svg' : 'assets/images/profil.svg'"
               alt="Profil"
               class="profil-img">
        </a>
      </div>

      <div class="mobile-menu-overlay"
       [class.active]="isMenuOpen"
       (click)="closeMenu()">
  </div>

  <aside class="mobile-menu" [class.active]="isMenuOpen">
  <div class="mobile-menu-content">
    <div class="menu-links">
      <a (click)="logout()" class="deconnected-link" (click)="closeMenu()">Se déconnecter</a>
      <a routerLink="/articles" routerLinkActive="active" (click)="closeMenu()">Articles</a>
      <a routerLink="/topics" routerLinkActive="active" (click)="closeMenu()">Thèmes</a>
    </div>
    <div class="menu-profile">
      <a routerLink="/user-profile" routerLinkActive="active" (click)="closeMenu()">
        <img src="assets/images/profil.svg" alt="Profil" class="profil-img">
      </a>
    </div>
  </div>
</aside>
    </nav>
  }

  `,
	styles: [
		`
    .navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.5rem 1rem;
      border-bottom: 2px solid #ccc;
    }


    .nav-links {
      display: flex;
      gap: 1rem;
      font-size: 20px;
      align-items: center;
    }

    .nav-links a {
      text-decoration: none;
      color: #333;
    }

    .nav-links .deconnected-link {
      color: #A40F0F;
    }

    .nav-links a.active {
      color: #6C5CCF;
    }

    .nav-links a.active .profil-img {
      color:  #6C5CCF;
}

    .logo-img {
      height: 81px;
      width: 140px;
    }

    .profil-img {
      height: 48px;
      width: 48px;
    }

    img {
      display: block;
    }

    .burger-button {
    display: none; /* Caché sur desktop */
    background: none;
    border: none;
    cursor: pointer;
    padding: 0.5rem;
  }

  .burger-icon {
    width: 24px;
    height: 16px;
  }

  .mobile-menu-overlay {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 98;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  .mobile-menu-overlay.active {
    opacity: 1;
    display: block;
  }

  .mobile-menu {
    position: fixed;
    top: 0;
    right: -184px; /* Caché par défaut */
    width: 184px;
    height: 100vh;
    background-color: #FFFFFF;
    z-index: 99;
    transition: transform 0.3s ease;
    padding: 1rem;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  }

  .mobile-menu.active {
    transform: translateX(-184px);
  }

  .mobile-menu-content {
    display: flex;
    flex-direction: column;
    height: 100%;
  }

  .menu-links {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-end;
    text-align: right;
    font-size: 20px;
  }

  .menu-links .deconnected-link {
    color: #A40F0F;
  }

  .menu-profile {
    margin-top: auto; /* Pousse le profil vers le bas */
    padding-top: 1rem;
    border-top: 1px solid #eee;
    display: flex;
    justify-content: flex-end;
  }

    /* Media query pour le responsive */
    @media (max-width: 768px) {
    .burger-button {
      display: block; /* Affiche le bouton burger sur mobile */
    }

    .nav-links {
      display: none; /* Cache les liens de navigation sur mobile */
    }

    .mobile-menu {
      display: flex;
      flex-direction: column;
    }
  }

  /* Media query pour le desktop */
  @media (min-width: 769px) {
    .mobile-menu-overlay,
    .mobile-menu {
      display: none; /* Cache le menu mobile sur desktop */
    }
  }
    `,
	],
})
export class NavbarComponent {
	isMenuOpen = false;
	authService = inject(AuthService);

	toggleMenu() {
		this.isMenuOpen = !this.isMenuOpen;
	}

	closeMenu() {
		this.isMenuOpen = false;
	}

	router = inject(Router);

	logout() {
		this.authService.logout().subscribe(() => {
			this.router.navigate(["/login"]);
		});
	}
}
