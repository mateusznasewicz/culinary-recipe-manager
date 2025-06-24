import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private apiUrl = 'http://localhost:8080/api/recipe';

  constructor(private http: HttpClient) {}

  getRecipeDetails(id: string): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.get(`${this.apiUrl}/${id}`, { headers });
  }

  addRecipe(recipeData: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.apiUrl}`, recipeData, { headers, responseType: 'text' });
  }

  searchRecipes(query: string, page: number, size: number): Observable<any> {
    const url = query
      ? `${this.apiUrl}?q=${encodeURIComponent(query)}&page=${page}&size=${size}`
      : `${this.apiUrl}?page=${page}&size=${size}`;
    return this.http.get(url);
  }

  searchRecipesByTags(tags: string[], page: number, size: number): Observable<any> {
    const params = tags.map(tag => `tags=${encodeURIComponent(tag)}`).join('&');
    return this.http.get(`${this.apiUrl}?${params}&page=${page}&size=${size}`);
  }

  getRecipesByUsername(username: string): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.get(`${this.apiUrl}?author=${username}`, { headers });
  }
}
