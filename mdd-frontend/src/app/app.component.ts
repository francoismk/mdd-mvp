import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { NavbarComponent } from "./shared/components/navbar/navbar.component";
import { CommonModule } from "@angular/common";

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
}
