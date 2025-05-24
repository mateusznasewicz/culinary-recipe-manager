import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private apiUrl = 'http://localhost:8082/api/recipe'
  constructor(private http: HttpClient) { }

  getTest(): Observable<string> {
    return this.http.get<string>(this.apiUrl);
  }
}
