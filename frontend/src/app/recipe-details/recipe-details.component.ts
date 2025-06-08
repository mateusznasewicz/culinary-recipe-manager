import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RecipeService } from '../service/recipe.service';
import { ReviewService } from '../service/review.service';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-recipe-details',
  templateUrl: './recipe-details.component.html',
  imports: [
    ReactiveFormsModule
  ],
  styleUrls: ['./recipe-details.component.scss']
})
export class RecipeDetailsComponent implements OnInit {
  recipe: any = null;
  reviews: any[] = [];
  reviewForm: FormGroup;
  isSubmitting = false;

  constructor(
    private route: ActivatedRoute,
    private recipeService: RecipeService,
    private reviewService: ReviewService,
    private fb: FormBuilder
  ) {
    this.reviewForm = this.fb.group({
      rating: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {
    const recipeId = this.route.snapshot.paramMap.get('id');
    if (recipeId) {
      this.loadRecipeDetails(recipeId);
      this.loadReviews(recipeId);
    }
  }

  loadRecipeDetails(id: string): void {
    this.recipeService.getRecipeDetails(id).subscribe({
      next: (data) => this.recipe = data,
      error: (err) => console.error('Error loading recipe details:', err)
    });
  }

  loadReviews(recipeId: string): void {
    this.reviewService.getReviewsByRecipe(recipeId).subscribe({
      next: (data) => this.reviews = data,
      error: (err) => console.error('Error loading reviews:', err)
    });
  }

  submitReview(): void {
    if (this.reviewForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const reviewData = { ...this.reviewForm.value, recipeId: this.recipe.id };

      this.reviewService.addReview(reviewData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.reviewForm.reset();
          this.loadReviews(this.recipe.id); // Reload reviews
        },
        error: (err) => {
          this.isSubmitting = false;
          console.error('Error submitting review:', err);
        }
      });
    }
  }
}
