import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RecipeService } from '../service/recipe.service';
import { AdminService, NamedEntity } from '../service/admin.service';
import { Observable } from 'rxjs';

interface Recipe {
  id: string;
  title: string;
  author: string;
  time?: number;
  portions?: number;
  tags: string[];
}

@Component({
  selector: 'app-recipe-search',
  templateUrl: './recipe-search.component.html',
  styleUrls: ['./recipe-search.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class RecipeSearchComponent implements OnInit {
  searchForm: FormGroup;
  recipes: Recipe[] = [];
  tagSearchResults: NamedEntity[] = [];
  isLoading = false;
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  tagInputValue = '';
  tagsControl: FormControl<string[]>;

  private recipeService: RecipeService = inject(RecipeService);
  private adminService: AdminService = inject(AdminService);
  private fb: FormBuilder = inject(FormBuilder);

  constructor() {
    this.searchForm = this.fb.group({
      query: [''],
      tags: [[] as string[]]
    });
    this.tagsControl = this.searchForm.get('tags') as FormControl<string[]>;
  }

  ngOnInit(): void {
    // Nie wywołujemy wyszukiwania na starcie
  }

  onTagInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.tagInputValue = input.value;
    if (this.tagInputValue.length >= 3) {
      this.adminService.searchTags(this.tagInputValue).subscribe({
        next: (results) => {
          this.tagSearchResults = results;
        },
        error: (err) => {
          console.error('Tag search error:', err);
          this.tagSearchResults = [];
        }
      });
    } else {
      this.tagSearchResults = [];
    }
  }

  addTag(tag: NamedEntity): void {
    const tags = this.tagsControl.value || [];
    if (!tags.includes(tag.name)) {
      this.tagsControl.setValue([...tags, tag.name]);
      this.tagSearchResults = [];
      this.tagInputValue = '';
      const input = document.querySelector('input[placeholder="Wpisz tag..."]') as HTMLInputElement;
      if (input) input.value = '';
    }
  }

  removeTag(tag: string): void {
    const tags = this.tagsControl.value || [];
    this.tagsControl.setValue(tags.filter((t: string) => t !== tag));
  }

  searchRecipes(): void {
    this.isLoading = true;
    const { query, tags } = this.searchForm.value;

    // Walidacja: co najmniej 3 znaki dla query
    if (query && query.length < 3) {
      this.recipes = [];
      this.isLoading = false;
      return;
    }

    let searchObservable: Observable<any>;
    if (tags?.length > 0 && tags.every((tag: string) => tag.trim())) {
      searchObservable = this.recipeService.searchRecipesByTags(tags, this.currentPage, this.pageSize);
    } else {
      const searchQuery = query ? query.trim() : '';
      searchObservable = this.recipeService.searchRecipes(searchQuery, this.currentPage, this.pageSize);
    }

    searchObservable.subscribe({
      next: (response) => {
        console.log('API response:', response);
        this.recipes = (response._embedded?.recipeDTOList || []).map((recipe: any) => ({
          id: recipe.id?.toString() || '',
          title: recipe.title,
          author: recipe.author,
          time: recipe.time,
          portions: recipe.portions,
          tags: recipe.tags || []
        }));
        this.totalPages = response.page?.totalPages || 1;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Recipe search error:', err);
        this.recipes = [];
        this.isLoading = false;
      }
    });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.searchRecipes();
    }
  }

  resetFilters(): void {
    this.searchForm.reset({
      query: '',
      tags: []
    });
    this.tagInputValue = '';
    this.currentPage = 0;
    this.recipes = [];
  }
}
