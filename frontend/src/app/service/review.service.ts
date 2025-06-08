import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8081/api/review';

  constructor(private http: HttpClient) {}

  getReviewsByRecipe(recipeId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}?recipeId=${recipeId}`);
  }

  addReview(review: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, review);
  }
}
