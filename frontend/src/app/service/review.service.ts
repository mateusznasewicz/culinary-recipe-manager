import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/review';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getReviewsByRecipe(recipeId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}?recipeId=${recipeId}`);
  }

  addReview(review: any): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<any>(this.apiUrl, review, { headers });
  }
}
