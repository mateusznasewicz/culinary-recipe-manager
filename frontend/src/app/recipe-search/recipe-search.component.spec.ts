import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { RecipeSearchComponent } from './recipe-search.component';
import { RecipeService } from '../service/recipe.service';
import { AdminService, NamedEntity } from '../service/admin.service';

describe('RecipeSearchComponent', () => {
  let component: RecipeSearchComponent;
  let fixture: ComponentFixture<RecipeSearchComponent>;
  let mockRecipeService: jasmine.SpyObj<RecipeService>;
  let mockAdminService: jasmine.SpyObj<AdminService>;

  const mockRecipeSearchResponse = {
    _embedded: {
      recipeDTOList: [
        {
          id: 1,
          title: 'Test Recipe 1',
          author: 'Author 1',
          time: 30,
          portions: 4,
          tags: ['tag1', 'tag2']
        },
        {
          id: 2,
          title: 'Test Recipe 2',
          author: 'Author 2',
          time: 45,
          portions: 6,
          tags: ['tag3']
        }
      ]
    },
    page: {
      totalPages: 2
    }
  };

  const mockTagSearchResults: NamedEntity[] = [
    { id: 1, name: 'obiad' },
    { id: 2, name: 'obiadowy' }
  ];

  beforeEach(async () => {
    const recipeServiceSpy = jasmine.createSpyObj('RecipeService', ['searchRecipes', 'searchRecipesByTags']);
    const adminServiceSpy = jasmine.createSpyObj('AdminService', ['searchTags']);

    await TestBed.configureTestingModule({
      imports: [RecipeSearchComponent, ReactiveFormsModule, RouterTestingModule],
      providers: [
        { provide: RecipeService, useValue: recipeServiceSpy },
        { provide: AdminService, useValue: adminServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeSearchComponent);
    component = fixture.componentInstance;
    mockRecipeService = TestBed.inject(RecipeService) as jasmine.SpyObj<RecipeService>;
    mockAdminService = TestBed.inject(AdminService) as jasmine.SpyObj<AdminService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should search recipes by query successfully', () => {
    // Arrange
    const query = 'test recipe';
    component.searchForm.patchValue({ query, tags: [] });
    mockRecipeService.searchRecipes.and.returnValue(of(mockRecipeSearchResponse));

    // Act
    component.searchRecipes();

    // Assert
    expect(mockRecipeService.searchRecipes).toHaveBeenCalledWith(query, 0, 10);
    expect(component.recipes).toEqual([
      {
        id: '1',
        title: 'Test Recipe 1',
        author: 'Author 1',
        time: 30,
        portions: 4,
        tags: ['tag1', 'tag2']
      },
      {
        id: '2',
        title: 'Test Recipe 2',
        author: 'Author 2',
        time: 45,
        portions: 6,
        tags: ['tag3']
      }
    ]);
    expect(component.totalPages).toBe(2);
    expect(component.isLoading).toBeFalse();
  });

  it('should search recipes by tags when tags are selected', () => {
    // Arrange
    const tags = ['obiad', 'szybki'];
    component.searchForm.patchValue({ query: '', tags });
    mockRecipeService.searchRecipesByTags.and.returnValue(of(mockRecipeSearchResponse));

    // Act
    component.searchRecipes();

    // Assert
    expect(mockRecipeService.searchRecipesByTags).toHaveBeenCalledWith(tags, 0, 10);
    expect(component.recipes.length).toBe(2);
    expect(component.isLoading).toBeFalse();
  });

  it('should handle tag search and add tags correctly', () => {
    // Arrange
    const mockEvent = {
      target: { value: 'obi' }
    } as unknown as Event;

    mockAdminService.searchTags.and.returnValue(of(mockTagSearchResults));

    // Act - simulate typing in tag input
    component.onTagInput(mockEvent);

    // Assert
    expect(mockAdminService.searchTags).toHaveBeenCalledWith('obi');
    expect(component.tagSearchResults).toEqual(mockTagSearchResults);

    // Act - add tag
    component.addTag(mockTagSearchResults[0]);

    // Assert
    expect(component.tagsControl.value).toContain('obiad');
    expect(component.tagSearchResults).toEqual([]);
    expect(component.tagInputValue).toBe('');
  });
});
