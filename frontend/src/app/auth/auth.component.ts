import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent {
  activeTab: 'login' | 'register' = 'login';
  loginForm: FormGroup;
  registerForm: FormGroup;
  showLoginPassword = false;
  showRegisterPassword = false;
  showConfirmPassword = false;
  passwordStrength = 0;
  isSubmitting = false;

  loginError = '';
  registerError = '';
  registerSuccess = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  switchTab(tab: 'login' | 'register') {
    this.activeTab = tab;
    this.clearMessages();
    this.loginForm.reset();
    this.registerForm.reset();
  }

  togglePasswordVisibility(field: string) {
    switch(field) {
      case 'login':
        this.showLoginPassword = !this.showLoginPassword;
        break;
      case 'register':
        this.showRegisterPassword = !this.showRegisterPassword;
        break;
      case 'confirm':
        this.showConfirmPassword = !this.showConfirmPassword;
        break;
    }
  }

  onPasswordInput() {
    const password = this.registerForm.get('password')?.value || '';
    this.passwordStrength = this.calculatePasswordStrength(password);
  }

  calculatePasswordStrength(password: string): number {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    return strength;
  }

  getPasswordStrengthClass(): string {
    switch(this.passwordStrength) {
      case 0:
      case 1: return 'strength-weak';
      case 2: return 'strength-fair';
      case 3:
      case 4: return 'strength-good';
      case 5: return 'strength-strong';
      default: return '';
    }
  }

  getPasswordStrengthWidth(): string {
    return `${(this.passwordStrength / 5) * 100}%`;
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    if (confirmPassword?.hasError('passwordMismatch')) {
      confirmPassword.setErrors(null);
    }

    return null;
  }

  onLogin() {
    if (this.loginForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.clearMessages();

      const { username, password } = this.loginForm.value;

      this.authService.login(username, password).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          console.log(response)
naviga          this.router.navigate(['/search']);
        },
        error: (error) => {
          console.log(error)
          this.isSubmitting = false;
          this.loginError = error.error?.message || 'Błąd logowania. Sprawdź dane.';
        }
      });
    }
  }

  onRegister() {
    if (this.registerForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.clearMessages();

      const { username, password, confirmPassword } = this.registerForm.value;

      this.authService.register(username, password, confirmPassword).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          this.registerSuccess = 'Rejestracja pomyślna! Możesz się teraz zalogować.';

          setTimeout(() => {
            this.registerForm.reset();
            this.passwordStrength = 0;
            this.switchTab('login');
          }, 2000);
        },
        error: (error) => {
          console.log(error)
          this.isSubmitting = false;
          this.registerError = error.error?.message || 'Błąd rejestracji. Spróbuj ponownie.';
        }
      });
    }
  }

  clearMessages() {
    this.loginError = '';
    this.registerError = '';
    this.registerSuccess = '';
  }

  // Getters for form validation
  get loginUsername() { return this.loginForm.get('username'); }
  get loginPassword() { return this.loginForm.get('password'); }
  get registerUsername() { return this.registerForm.get('username'); }
  get registerPassword() { return this.registerForm.get('password'); }
  get confirmPassword() { return this.registerForm.get('confirmPassword'); }
}
