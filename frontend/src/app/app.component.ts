import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from './service/auth.service';
import { RouterModule } from '@angular/router';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [RouterModule, NgIf]
})
export class AppComponent implements OnInit {
  currentUrl: string = '';

  constructor(public authService: AuthService, public router: Router) {}

  ngOnInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.currentUrl = event.url;
      }
    });

    this.authService.currentUser$.subscribe(username => {
      if (username) {
        this.authService.checkAdminStatus();
      }
    });
  }
  shouldShowNavBar(): boolean {
    return this.router.url !== '/auth';
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }
}
