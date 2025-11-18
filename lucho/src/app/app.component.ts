import { Component, HostListener } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule],
  templateUrl: './app.component.html'
})
export class AppComponent {
  hasScrolled = false;

  @HostListener('window:scroll', [])
  onScroll(): void {
    this.hasScrolled = window.scrollY > 10;
  }
}
