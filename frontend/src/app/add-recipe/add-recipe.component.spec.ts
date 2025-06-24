import { TestBed, ComponentFixture } from '@angular/core/testing';
import { AddRecipeComponent } from './add-recipe.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AdminService, NamedEntity } from '../service/admin.service';
import { AuthService } from '../service/auth.service';
import { RecipeService } from '../service/recipe.service';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideLocationMocks } from '@angular/common/testing';
import { BehaviorSubject, of } from 'rxjs';
import { CommonModule } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';

describe('AddRecipeComponent', () => {
  let component: AddRecipeComponent;
  let fixture: ComponentFixture<AddRecipeComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockAdminService: jasmine.SpyObj<AdminService>;
  let mockRecipeService: jasmine.SpyObj<RecipeService>;

  beforeEach(async () => {
    // Mock AuthService
    mockAuthService = jasmine.createSpyObj<AuthService>('AuthService', ['isLoggedIn', 'getCurrentUserId'], {
      currentUser$: new BehaviorSubject<string | null>(null)
    });
    mockAuthService.isLoggedIn.and.returnValue(true);
    mockAuthService.getCurrentUserId.and.returnValue(1);

    // Mock AdminService
    mockAdminService = jasmine.createSpyObj<AdminService>('AdminService', ['searchTags', 'searchIngredients', 'searchUnits']);
    mockAdminService.searchTags.and.returnValue(of([]));
    mockAdminService.searchIngredients.and.returnValue(of([]));
    mockAdminService.searchUnits.and.returnValue(of([]));

    // Mock RecipeService
    mockRecipeService = jasmine.createSpyObj<RecipeService>('RecipeService', ['addRecipe']);
    mockRecipeService.addRecipe.and.returnValue(of('Recipe added'));

    await TestBed.configureTestingModule({
      imports: [
        AddRecipeComponent,
        ReactiveFormsModule,
        CommonModule
      ],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: mockAuthService },
        { provide: AdminService, useValue: mockAdminService },
        { provide: RecipeService, useValue: mockRecipeService },
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddRecipeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize recipeForm with required fields', () => {
    expect(component.recipeForm).toBeTruthy();
    expect(component.recipeForm.get('title')).toBeTruthy();
    expect(component.recipeForm.get('description')).toBeTruthy();
    expect(component.recipeForm.get('time')).toBeTruthy();
    expect(component.recipeForm.get('portions')).toBeTruthy();
    expect(component.recipeForm.get('difficulty')).toBeTruthy();
    expect(component.recipeForm.get('tags')).toBeTruthy();
    expect(component.recipeForm.get('recipeSteps')).toBeTruthy();
    expect(component.recipeForm.get('ingredientUnits')).toBeTruthy();
  });

  it('should initialize with one step and one ingredient unit', () => {
    expect(component.recipeSteps.length).toBe(1); // Jeden krok dodany w konstruktorze
    expect(component.ingredientUnits.length).toBe(1); // Jeden składnik dodany w konstruktorze
    expect(component.recipeSteps.at(0).get('stepNumber')?.value).toBe(1);
    expect(component.ingredientUnits.at(0).get('ingredientName')).toBeTruthy();
  });
});
