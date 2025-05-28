import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getIngredients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ingredient`, { headers: this.getAuthHeaders() });
  }

  getUnits(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/unit`, { headers: this.getAuthHeaders() });
  }

  getTags(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/tag`, { headers: this.getAuthHeaders() });
  }

  updateIngredient(data: { id: number, name: string }): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/ingredient`, data, { headers: this.getAuthHeaders() });
  }

  updateUnit(data: { id: number, name: string }): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/unit`, data, { headers: this.getAuthHeaders() });
  }

  updateTag(data: { id: number, name: string }): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/tag`, data, { headers: this.getAuthHeaders() });
  }

  deleteIngredient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/ingredient?id=${id}`, { headers: this.getAuthHeaders() });
  }

  deleteUnit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/unit?id=${id}`, { headers: this.getAuthHeaders() });
  }

  deleteTag(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tag?id=${id}`, { headers: this.getAuthHeaders() });
  }
  addIngredient(data: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/ingredient`, data, { headers: this.getAuthHeaders() });
  }

  addUnit(data: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/unit`, data, { headers: this.getAuthHeaders() });
  }

  addTag(data: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/tag`, data, { headers: this.getAuthHeaders() });
  }
}
