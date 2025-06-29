import { Component, afterNextRender, effect, inject } from "@angular/core";

import { AuthService } from "./features/auth/services/auth.service";
import { CommonModule } from "@angular/common";
import { NavbarComponent } from "./shared/components/navbar/navbar.component";
import { RouterOutlet } from "@angular/router";

@Component({
	selector: "app-root",
	standalone: true,
	imports: [RouterOutlet, NavbarComponent, CommonModule],
	template: `
    <app-navbar></app-navbar>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
    `,
	styles: [
		`
    // .main-content {
    //   padding: 20px;
    //   max-width: 1200px;
    //   margin: 0 auto;
    //   width: 100%;
    // }
    `,
	],
})
export class AppComponent {
	title = "mdd-frontend";
	authService = inject(AuthService);

	constructor() {
		afterNextRender(() => {
			this.authService.checkAuthStatus().subscribe();
		});
	}
}
