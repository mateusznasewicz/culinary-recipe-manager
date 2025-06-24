import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RecipeDetailsComponent } from './recipe-details.component';
import { RecipeService } from '../service/recipe.service';
import { ReviewService } from '../service/review.service';
import { AuthService } from '../service/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

describe('RecipeDetailsComponent', () => {
  let component: RecipeDetailsComponent;
  let fixture: ComponentFixture<RecipeDetailsComponent>;
  let mockRecipeService: jasmine.SpyObj<RecipeService>;
  let mockReviewService: jasmine.SpyObj<ReviewService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockActivatedRoute: any;

  const mockRecipe = {
    id: '1',
    title: 'Test Recipe',
    author: 'Test Author',
    averageRating: 4.5,
    time: 30,
    portions: 4,
    difficulty: 'łatwy',
    description: 'Test description',
    ingredients: ['składnik 1', 'składnik 2'],
    steps: ['krok 1', 'krok 2'],
    tags: ['tag1', 'tag2']
  };

  const mockReviews = [
    { id: '1', rating: 5, comment: 'Świetny przepis!', author: 'User1' },
    { id: '2', rating: 4, comment: 'Bardzo dobry', author: 'User2' }
  ];

  beforeEach(async () => {
    const recipeServiceSpy = jasmine.createSpyObj('RecipeService', ['getRecipeDetails']);
    const reviewServiceSpy = jasmine.createSpyObj('ReviewService', ['getReviewsByRecipe', 'addReview']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', [
      'isLoggedIn',
      'getCurrentUserId',
      'getCurrentUsernameFromToken',
      'getToken'
    ]);

    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.returnValue('1')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [RecipeDetailsComponent, ReactiveFormsModule],
      providers: [
        { provide: RecipeService, useValue: recipeServiceSpy },
        { provide: ReviewService, useValue: reviewServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeDetailsComponent);
    component = fixture.componentInstance;
    mockRecipeService = TestBed.inject(RecipeService) as jasmine.SpyObj<RecipeService>;
    mockReviewService = TestBed.inject(ReviewService) as jasmine.SpyObj<ReviewService>;
    mockAuthService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load recipe details and reviews on init', () => {
    // Arrange
    mockRecipeService.getRecipeDetails.and.returnValue(of(mockRecipe));
    mockReviewService.getReviewsByRecipe.and.returnValue(of(mockReviews));

    // Act
    component.ngOnInit();

    // Assert
    expect(mockRecipeService.getRecipeDetails).toHaveBeenCalledWith('1');
    expect(mockReviewService.getReviewsByRecipe).toHaveBeenCalledWith('1');
    expect(component.recipe).toEqual(mockRecipe);
    expect(component.reviews).toEqual(mockReviews);
    expect(component.isLoading).toBeFalse();
  });

  it('should submit review successfully when user is logged in', () => {
    // Arrange
    component.recipe = mockRecipe;
    component.reviewForm.patchValue({
      rating: 5,
      comment: 'Bardzo dobry przepis, polecam wszystkim!'
    });

    mockAuthService.isLoggedIn.and.returnValue(true);
    mockAuthService.getCurrentUserId.and.returnValue(123);
    mockAuthService.getToken.and.returnValue('mock-token');
    mockReviewService.addReview.and.returnValue(of({}));
    mockReviewService.getReviewsByRecipe.and.returnValue(of(mockReviews));

    spyOn(window, 'alert');

    // Act
    component.submitReview();

    // Assert
    expect(mockReviewService.addReview).toHaveBeenCalledWith({
      userId: 123,
      recipeId: 1,
      rating: 5,
      comment: 'Bardzo dobry przepis, polecam wszystkim!'
    });
    expect(component.isSubmitting).toBeFalse();
    expect(component.reviewForm.value.rating).toBeNull();
    expect(component.reviewForm.value.comment).toBeNull();
    expect(window.alert).toHaveBeenCalledWith('Recenzja została dodana pomyślnie!');
  });

  it('should handle review submission error and show 201 as success', () => {
    // Arrange
    component.recipe = mockRecipe;
    component.reviewForm.patchValue({
      rating: 4,
      comment: 'Dobry przepis, ale można poprawić'
    });

    mockAuthService.isLoggedIn.and.returnValue(true);
    mockAuthService.getCurrentUserId.and.returnValue(456);
    mockAuthService.getToken.and.returnValue('mock-token');

    const mockError = new HttpErrorResponse({
      status: 201,
      statusText: 'Created',
      error: 'Mock 201 error'
    });

    mockReviewService.addReview.and.returnValue(throwError(() => mockError));
    mockReviewService.getReviewsByRecipe.and.returnValue(of(mockReviews));

    spyOn(window, 'alert');

    // Act
    component.submitReview();

    // Assert
    expect(component.isSubmitting).toBeFalse();
    expect(component.reviewForm.value.rating).toBeNull();
    expect(component.reviewForm.value.comment).toBeNull();
    expect(window.alert).toHaveBeenCalledWith('Recenzja została dodana pomyślnie, mimo błędu parsowania odpowiedzi.');
    expect(mockReviewService.getReviewsByRecipe).toHaveBeenCalledWith('1');
  });
});
