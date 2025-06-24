import { TestBed, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from './service/auth.service';
import { provideRouter } from '@angular/router';
import { provideLocationMocks } from '@angular/common/testing';
import { BehaviorSubject } from 'rxjs';
import { By } from '@angular/platform-browser';
import { NgIf } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let routerEventsSubject: BehaviorSubject<NavigationEnd>;
  let mockUrl: string;

  beforeEach(async () => {
    routerEventsSubject = new BehaviorSubject(new NavigationEnd(0, '/home', '/home'));

    mockUrl = '/home';
    mockRouter = jasmine.createSpyObj<Router>('Router', ['navigate'], {
      events: routerEventsSubject.asObservable()
    });
    Object.defineProperty(mockRouter, 'url', {
      get: () => mockUrl,
      configurable: true
    });


    mockAuthService = jasmine.createSpyObj<AuthService>('AuthService', ['logout', 'isAdmin', 'checkAdminStatus'], {
      currentUser$: new BehaviorSubject<string | null>(null)
    });
    mockAuthService.isAdmin.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [
        AppComponent,
        NgIf,
        HttpClientTestingModule
      ],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: AuthService, useValue: mockAuthService },
        provideRouter([]),
        provideLocationMocks()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app component', () => {
    expect(component).toBeTruthy();
  });


  it('should not render the navbar when on /auth', () => {
    mockUrl = '/auth';
    routerEventsSubject.next(new NavigationEnd(0, '/auth', '/auth'));
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.navbar'))).toBeNull();
  });
});
