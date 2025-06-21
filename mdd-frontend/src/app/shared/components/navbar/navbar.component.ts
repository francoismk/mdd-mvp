import { Component } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-navbar",
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar">
      <div class="logo">
        <a routerLink="/"><img src="assets/images/logo.svg" alt="MDD Logo" class="logo-img"></a>
      </div>
      <div class="nav-links">
        <a routerLink="/" class="deconnected-link">Se déconnecter</a>
        <a routerLink="/articles" routerLinkActive="active">Articles</a>
        <a routerLink="/topics" routerLinkActive="active">Thèmes</a>
        <a routerLink="/user-profile" routerLinkActive="active" #profileLink="routerLinkActive">
        <img [src]="profileLink.isActive ? 'assets/images/profil-active.svg' : 'assets/images/profil.svg'" 
               alt="Profil" 
               class="profil-img">
        </a>
      </div>
    </nav>
    
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
    `
  ],
})
export class NavbarComponent {
  logout() {
    console.log("logout");
  }
}
