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
  private apiUrl = 'http://localhost:8080/api';
  private adminApiUrl = 'http://localhost:8082/api';
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
    throw new Error("Not implemented")
  }

  updateUnit(data: { id: number, name: string }): Observable<void> {
    throw new Error("Not implemented")
  }

  updateTag(data: { id: number, name: string }): Observable<void> {
    throw new Error("Not implemented")
  }

  deleteIngredient(id: number): Observable<void> {
    throw new Error("Not implemented")
  }

  deleteUnit(id: number): Observable<void> {
    throw new Error("Not implemented")
  }

  deleteTag(id: number): Observable<void> {
    throw new Error("Not implemented");
  }

  addIngredient(data: { name: string }): Observable<string> {
    return this.http.post<string>(`${this.adminApiUrl}/ingredient`, data, {
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json'})
  }

  addUnit(data: { name: string }): Observable<string> {
    return this.http.post<string>(`${this.adminApiUrl}/unit`, data, { headers: this.getAuthHeaders(),
      responseType: 'text' as 'json' });
  }

  addTag(data: { name: string }): Observable<string> {
    return this.http.post<string>(`${this.adminApiUrl}/tag`, data, { headers: this.getAuthHeaders(),
      responseType: 'text' as 'json' });
  }
}
