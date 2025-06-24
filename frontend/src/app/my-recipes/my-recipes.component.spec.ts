import { TestBed, ComponentFixture } from '@angular/core/testing';
import { MyRecipesComponent } from './my-recipes.component';
import { AuthService } from '../service/auth.service';
import { RecipeService } from '../service/recipe.service';
import {  provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideLocationMocks } from '@angular/common/testing';
import { BehaviorSubject, of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

describe('MyRecipesComponent', () => {
  let component: MyRecipesComponent;
  let fixture: ComponentFixture<MyRecipesComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRecipeService: jasmine.SpyObj<RecipeService>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj<AuthService>('AuthService', ['getCurrentUsername'], {
      currentUser$: new BehaviorSubject<string | null>(null)
    });
    mockAuthService.getCurrentUsername.and.returnValue('testuser');

    mockRecipeService = jasmine.createSpyObj<RecipeService>('RecipeService', ['getRecipesByUsername']);
    mockRecipeService.getRecipesByUsername.and.returnValue(of({
      _embedded: {
        recipeDTOList: [
          { id: '1', title: 'Recipe 1', author: 'testuser', time: 30, tags: ['tag1'] },
          { id: '2', title: 'Recipe 2', author: 'testuser', time: 45, tags: ['tag2'] }
        ]
      }
    }));

    await TestBed.configureTestingModule({
      imports: [
        MyRecipesComponent,
        CommonModule,
        RouterLink
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: RecipeService, useValue: mockRecipeService },
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MyRecipesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize recipes and set isLoading to false in ngOnInit', () => {
    component.ngOnInit();
    fixture.detectChanges();

    expect(mockAuthService.getCurrentUsername).toHaveBeenCalled();
    expect(mockRecipeService.getRecipesByUsername).toHaveBeenCalledWith('testuser');
    expect(component.recipes).toEqual([
      { id: '1', title: 'Recipe 1', author: 'testuser', time: 30, tags: ['tag1'] },
      { id: '2', title: 'Recipe 2', author: 'testuser', time: 45, tags: ['tag2'] }
    ]);
    expect(component.isLoading).toBeFalse();
  });

  it('should display correct content based on state', () => {
    // Stan 1: isLoading = true
    component.isLoading = true;
    component.recipes = [];
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.text-center.p-4.text-gray-700')).nativeElement.textContent).toContain('Ładowanie...');
    expect(fixture.debugElement.query(By.css('.culinary-result-card'))).toBeNull();

    // Stan 2: isLoading = false, recipes = []
    component.isLoading = false;
    component.recipes = [];
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.text-center.text-gray-700.mb-8')).nativeElement.textContent).toContain('Nie masz jeszcze żadnych przepisów');
    expect(fixture.debugElement.query(By.css('.culinary-result-card'))).toBeNull();

    // Stan 3: isLoading = false, recipes zawiera dane
    component.isLoading = false;
    component.recipes = [
      { id: '1', title: 'Recipe 1', author: 'testuser', time: 30, tags: ['tag1'] },
      { id: '2', title: 'Recipe 2', author: 'testuser', time: 45, tags: ['tag2'] }
    ];
    fixture.detectChanges();
    const recipeElements = fixture.debugElement.queryAll(By.css('.culinary-result-card'));
    expect(recipeElements.length).toBe(2);
    expect(recipeElements[0].query(By.css('.text-xl')).nativeElement.textContent).toContain('Recipe 1');
    expect(recipeElements[1].query(By.css('.text-xl')).nativeElement.textContent).toContain('Recipe 2');
    expect(fixture.debugElement.query(By.css('.text-center.p-4.text-gray-700'))).toBeNull();
    expect(fixture.debugElement.query(By.css('.text-center.text-gray-700.mb-8'))).toBeNull();
  });
});
