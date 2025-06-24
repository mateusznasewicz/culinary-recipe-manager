import { TestBed, ComponentFixture } from '@angular/core/testing';
import { AuthComponent } from './auth.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideLocationMocks } from '@angular/common/testing';
import { BehaviorSubject, of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';

describe('AuthComponent', () => {
  let component: AuthComponent;
  let fixture: ComponentFixture<AuthComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    // Mock AuthService
    mockAuthService = jasmine.createSpyObj<AuthService>('AuthService', ['login', 'register'], {
      currentUser$: new BehaviorSubject<string | null>(null)
    });
    mockAuthService.login.and.returnValue(of({ token: 'mock-token' }));
    mockAuthService.register.and.returnValue(of('Registration successful'));

    // Mock Router
    mockRouter = jasmine.createSpyObj<Router>('Router', ['navigate', 'navigateByUrl']);
    mockRouter.navigate.and.returnValue(Promise.resolve(true));
    mockRouter.navigateByUrl.and.returnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [
        AuthComponent,
        ReactiveFormsModule,
        CommonModule
      ],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AuthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize loginForm and registerForm with required fields', () => {
    // Sprawdzenie loginForm
    expect(component.loginForm).toBeTruthy();
    expect(component.loginForm.get('username')).toBeTruthy();
    expect(component.loginForm.get('password')).toBeTruthy();

    // Sprawdzenie registerForm
    expect(component.registerForm).toBeTruthy();
    expect(component.registerForm.get('username')).toBeTruthy();
    expect(component.registerForm.get('password')).toBeTruthy();
    expect(component.registerForm.get('confirmPassword')).toBeTruthy();

    // Sprawdzenie, czy registerForm ma walidator
    expect(component.registerForm.validator).toBeTruthy();

    // Weryfikacja działania walidatora passwordMatchValidator
    component.registerForm.setValue({
      username: 'testuser',
      password: 'password123',
      confirmPassword: 'different'
    });
    expect(component.registerForm.hasError('passwordMismatch')).toBeTrue();

    component.registerForm.setValue({
      username: 'testuser',
      password: 'password123',
      confirmPassword: 'password123'
    });
    expect(component.registerForm.hasError('passwordMismatch')).toBeFalse();
  });

  it('should switch between tabs and reset forms', () => {
    // Początkowo activeTab to 'login'
    expect(component.activeTab).toBe('login');
    expect(fixture.debugElement.query(By.css('.tab-active')).nativeElement.textContent).toContain('Logowanie');

    // na rejestrację
    component.switchTab('register');
    fixture.detectChanges();
    expect(component.activeTab).toBe('register');
    expect(fixture.debugElement.query(By.css('.tab-active')).nativeElement.textContent).toContain('Rejestracja');
    expect(component.loginForm.pristine).toBeTrue();
    expect(component.registerForm.pristine).toBeTrue();
    expect(component.loginError).toBe('');
    expect(component.registerError).toBe('');
    expect(component.registerSuccess).toBe('');
  });

  it('should call login and navigate on valid login form submission', () => {

    component.loginForm.setValue({
      username: 'user',
      password: 'TwojaStara123'
    });

    component.onLogin();
    fixture.detectChanges();

    expect(mockAuthService.login).toHaveBeenCalledWith('user', 'TwojaStara123');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/search']);
    expect(component.isSubmitting).toBeFalse();
  });
});
