import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RecipeService } from '../service/recipe.service';
import { ReviewService } from '../service/review.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

interface Recipe {
  id: string;
  title: string;
  author: string;
  averageRating?: number;
  time?: number;
  portions?: number;
  difficulty?: string;
  description?: string;
  ingredients: string[];
  steps: string[];
  tags: string[];
}

interface Review {
  id?: string;
  userId?: number; // Dla wysyłania do commandservice
  recipeId?: string;
  rating: number;
  comment: string;
  author: string; // Dla wyświetlania z queryservice
}
interface ReviewResponse {
  _embedded?: {
    reviewDTOList?: Review[];
  };
}
@Component({
  selector: 'app-recipe-details',
  templateUrl: './recipe-details.component.html',
  imports: [
    ReactiveFormsModule,
    CommonModule
  ],
  styleUrls: ['./recipe-details.component.scss']
})
export class RecipeDetailsComponent implements OnInit {
  recipe: Recipe | null = null;
  reviews: Review[] = [];
  reviewForm: FormGroup;
  isSubmitting = false;
  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private recipeService: RecipeService,
    private reviewService: ReviewService,
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.reviewForm = this.fb.group({
      rating: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    const recipeId = this.route.snapshot.paramMap.get('id');
    console.log('Recipe ID from URL:', recipeId); // Debugowanie recipeId
    if (recipeId) {
      this.loadRecipeDetails(recipeId);
      this.loadReviews(recipeId);
    } else {
      this.errorMessage = 'Nie znaleziono ID przepisu w adresie URL';
      this.isLoading = false;
    }
  }

  loadRecipeDetails(id: string): void {
    this.isLoading = true;
    this.recipeService.getRecipeDetails(id).subscribe({
      next: (data) => {
        console.log('Raw data from backend:', data); // Logowanie surowych danych
        // Obsługa różnych nazw pola id
        const recipeId = data.id || data.recipeId || data._id || id;
        if (!recipeId) {
          console.error('No valid ID found in recipe data');
          this.errorMessage = 'Nie udało się załadować przepisu: brak ID';
          this.isLoading = false;
          return;
        }

        this.recipe = {
          id: recipeId,
          title: data.title || 'Brak tytułu',
          author: data.author || 'Nieznany autor',
          averageRating: data.averageRating,
          time: data.time,
          portions: data.portions,
          difficulty: data.difficulty,
          description: data.description,
          ingredients: this.convertToArray(data.ingredients),
          steps: this.convertToArray(data.steps),
          tags: this.convertToArray(data.tags)
        };

        console.log('Processed recipe:', this.recipe);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading recipe details:', err);
        this.errorMessage = `Nie udało się załadować przepisu: ${err.message || 'Nieznany błąd'}`;
        this.isLoading = false;
      }
    });
  }

  loadReviews(recipeId: string): void {
    this.reviewService.getReviewsByRecipe(recipeId).subscribe({
      next: (data: Review[] | ReviewResponse) => {
        this.reviews = Array.isArray(data) ? data : (data._embedded?.reviewDTOList || []);
        console.log('Reviews loaded:', this.reviews);
      },
      error: (err) => {
        console.error('Error loading reviews:', err);
        this.reviews = [];
        console.log('Reviews load failed, reviews set to empty:', this.reviews);
      }
    });
  }

  submitReview(): void {
    console.log('=== SUBMIT REVIEW DEBUG ===');
    console.log('Form valid:', this.reviewForm.valid);
    console.log('Form value:', this.reviewForm.value);
    console.log('Is submitting:', this.isSubmitting);
    console.log('Recipe:', this.recipe);
    console.log('Is logged in:', this.authService.isLoggedIn());
    console.log('Username from token:', this.authService.getCurrentUsernameFromToken());

    if (!this.reviewForm.valid) {
      console.log('Form is invalid - marking all fields as touched');
      this.reviewForm.markAllAsTouched();
      return;
    }

    if (this.isSubmitting) {
      console.log('Already submitting, ignoring request');
      return;
    }

    if (!this.recipe?.id) {
      console.log('No recipe ID available');
      alert('Błąd: Brak ID przepisu');
      return;
    }

    if (!this.authService.isLoggedIn()) {
      console.log('User not logged in');
      alert('Błąd: Musisz być zalogowany, aby dodać recenzję. Upewnij się, że masz ważny token JWT.');
      return;
    }

    this.isSubmitting = true;

    const reviewData: any = {
      userId: this.authService.getCurrentUserId(),
      recipeId: parseInt(this.recipe.id), // Konwersja na liczbę
      rating: Number(this.reviewForm.get('rating')?.value), // Backend akceptuje BigDecimal
      comment: this.reviewForm.get('comment')?.value?.trim()
    };
    console.log('Sending review data:', reviewData);
    console.log('Authorization header:', `Bearer ${this.authService.getToken()}`);

    this.reviewService.addReview(reviewData).subscribe({
      next: (response) => {
        console.log('Review added successfully:', response);
        this.isSubmitting = false;
        this.reviewForm.reset();
        this.loadReviews(this.recipe!.id);
        alert('Recenzja została dodana pomyślnie!');
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error submitting review:', err);
        this.isSubmitting = false;

        let errorMsg = 'Wystąpił błąd podczas dodawania recenzji.';
        if (err.status === 201) {
          // Obsługa 201 Created jako sukcesu
          console.log('Received 201 Created, assuming review was added');
          this.reviewForm.reset();
          this.loadReviews(this.recipe!.id);
          alert('Recenzja została dodana pomyślnie, mimo błędu parsowania odpowiedzi.');
        } else {
          if (err.status) {
            errorMsg += ` Status: ${err.status}`;
          }
          if (err.error) {
            try {
              // Próbuj sparsować błąd jako JSON
              errorMsg += ` Szczegóły: ${JSON.stringify(err.error)}`;
            } catch (e) {
              // Jeśli nie JSON, wyświetl jako tekst
              errorMsg += ` Szczegóły: ${err.error.text || err.error || 'Brak szczegółów'}`;
            }
          }
          if (err.message) {
            errorMsg += ` Wiadomość: ${err.message}`;
          }

          // Pobierz wszystkie nagłówki odpowiedzi
          const headers: { [key: string]: string[] | null } = {};
          err.headers.keys().forEach(key => {
            headers[key] = err.headers.getAll(key);
          });

          console.log('Full error response:', {
            status: err.status,
            statusText: err.statusText,
            error: err.error,
            message: err.message,
            headers: headers
          });

          alert(errorMsg);
        }
      }
    });
  }

  private convertToArray(data: any): string[] {
    if (Array.isArray(data)) {
      return data.filter(item => item != null).map(item => String(item));
    }

    if (data && typeof data === 'object') {
      return Object.values(data).filter(item => item != null).map(item => String(item));
    }

    if (data && typeof data === 'string') {
      return data.split(/[,;|]/).map(item => item.trim()).filter(item => item.length > 0);
    }

    return [];
  }

  hasFieldError(fieldName: string): boolean {
    const field = this.reviewForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.reviewForm.get(fieldName);

    if (field?.errors) {
      if (field.errors['required']) {
        return this.getRequiredMessage(fieldName);
      }
      if (field.errors['minlength']) {
        const minLength = field.errors['minlength'].requiredLength;
        return `${this.getFieldLabel(fieldName)} musi mieć minimum ${minLength} znaków`;
      }
      if (field.errors['maxlength']) {
        const maxLength = field.errors['maxlength'].requiredLength;
        return `${this.getFieldLabel(fieldName)} może mieć maksymalnie ${maxLength} znaków`;
      }
      if (field.errors['min']) {
        return 'Minimalna ocena to 1';
      }
      if (field.errors['max']) {
        return 'Maksymalna ocena to 5';
      }
    }

    return '';
  }

  private getRequiredMessage(fieldName: string): string {
    switch (fieldName) {
      case 'rating': return 'Ocena jest wymagana';
      case 'comment': return 'Komentarz jest wymagany';
      default: return 'To pole jest wymagane';
    }
  }

  private getFieldLabel(fieldName: string): string {
    switch (fieldName) {
      case 'rating': return 'Ocena';
      case 'comment': return 'Komentarz';
      default: return 'Pole';
    }
  }

  get Array() {
    return Array;
  }
}
