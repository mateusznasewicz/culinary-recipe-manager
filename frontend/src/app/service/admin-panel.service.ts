// src/app/service/admin.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  addIngredient(data: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/ingredient`, data);
  }

  addUnit(data: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/unit`, data);
  }

  addTag(data: { name: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/tag`, data);
  }
}
