import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface NamedEntity {
  id: number;
  name: string;
}

export interface IngredientUnit {
  quantity: number;
  unit: NamedEntity;
  ingredient: NamedEntity;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private adminapiUrl = 'http://localhost:8080/api';
  private apiUrl = 'http://localhost:8081/api'
  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  searchIngredients(q: String): Observable<NamedEntity[]> {
    return this.http.get<NamedEntity[]>(`${this.apiUrl}/ingredient?q=${q}`)
  }

  searchTags(q: String): Observable<NamedEntity[]> {
    return this.http.get<NamedEntity[]>(`${this.apiUrl}/tag?q=${q}`)
  }

  searchUnits(q: String): Observable<NamedEntity[]> {
    return this.http.get<NamedEntity[]>(`${this.apiUrl}/unit?q=${q}`);
  }

  updateIngredient(data: { id: number, name: string }): Observable<void> {
    return this.http.put<void>(`${this.adminapiUrl}/ingredient`, data, {
      headers: this.getAuthHeaders(),
      params: { id: data.id.toString() }
    });
  }

  updateUnit(data: { id: number, name: string }): Observable<void> {
    return this.http.put<void>(`${this.adminapiUrl}/unit`, data, {
      headers: this.getAuthHeaders(),
      params: { id: data.id.toString() }
    });
  }

  updateTag(data: { id: number, name: string }): Observable<void> {
    return this.http.put<void>(`${this.adminapiUrl}/tag`, data, {
      headers: this.getAuthHeaders(),
      params: { id: data.id.toString() }
    });
  }

  deleteIngredient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminapiUrl}/ingredient`, {
      headers: this.getAuthHeaders(),
      params: { id: id.toString() }
    });
  }

  deleteUnit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminapiUrl}/unit`, {
      headers: this.getAuthHeaders(),
      params: { id: id.toString() }
    });
  }

  deleteTag(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminapiUrl}/tag`, {
      headers: this.getAuthHeaders(),
      params: { id: id.toString() }
    });
  }

  addIngredient(data: { name: string }): Observable<string> {
    return this.http.post<string>(`${this.adminapiUrl}/ingredient`, data, {
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json'})
  }

  addUnit(data: { name: string }): Observable<string> {
    return this.http.post<string>(`${this.adminapiUrl}/unit`, data, {
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json'
    });
  }

  addTag(data: { name: string }): Observable<string> {
    return this.http.post<string>(`${this.adminapiUrl}/tag`, data, {
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json'
    });
  }
}
