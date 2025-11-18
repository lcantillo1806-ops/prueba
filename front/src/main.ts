import 'zone.js'; 
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error(err));

  document.addEventListener('scroll', () => {
  const titles = document.querySelectorAll('.sticky-title');

  titles.forEach(title => {
    const parent = title.parentElement;
    if (!parent) return;

    if (parent.getBoundingClientRect().top < 0) {
      title.classList.add('scrolled');
    } else {
      title.classList.remove('scrolled');
    }
  });
});
