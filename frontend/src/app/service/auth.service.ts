import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
}

export interface AuthResponse {
  token: string;
  tokenType?: string;
  message?: string;
}

export interface TextResponse {
  text: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private currentUserSubject = new BehaviorSubject<string | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedUsername = localStorage.getItem('username');
    if (storedUsername) {
      this.currentUserSubject.next(storedUsername);
    }
  }

  login(username: string, password: string): Observable<AuthResponse> {
    const loginData: LoginRequest = { username, password };

    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginData, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('username', username);
          this.currentUserSubject.next(username);
          console.log('Token saved:', response.token);
          this.checkAdminStatus(); // Sprawdzenie roli po zalogowaniu
        }
      })
    );
  }

  register(username: string, password: string, confirmPassword: string): Observable<string> {
    const registerData: RegisterRequest = { username, password, confirmPassword };

    return this.http.post<string>(`${this.apiUrl}/register`, registerData, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }), responseType: 'text' as 'json'
    }).pipe(
      tap(() => {
        localStorage.setItem('username', username);
        this.currentUserSubject.next(username);
        this.checkAdminStatus(); // Sprawdzenie roli po rejestracji
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    const isLogged = !!token;
    console.log('Is logged in:', isLogged, 'Token exists:', !!token);
    return isLogged;
  }

  getToken(): string | null {
    const token = localStorage.getItem('token');
    console.log('Retrieved token:', token);
    return token;
  }

  getCurrentUsername(): string | null {
    const username = localStorage.getItem('username');
    console.log('Retrieved username:', username);
    return username;
  }

  getCurrentUserId(): number | null {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        return decoded.userid ? parseInt(decoded.userid) : null;
      } catch (error) {
        return null;
      }
    }
    return null;
  }

  getCurrentUsernameFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        if (decoded.sub) {
          return decoded.sub;
        }
        return null;
      } catch (error) {
        return null;
      }
    }
    return null;
  }

  isAdmin(): boolean {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        return decoded.role === 'ROLE_ADMIN';
      } catch (error) {
        return false;
      }
    }
    return false;
  }

  checkAdminStatus() {
    this.currentUserSubject.next(this.getCurrentUsername());
    console.log('Admin status checked:', this.isAdmin());
  }
}
