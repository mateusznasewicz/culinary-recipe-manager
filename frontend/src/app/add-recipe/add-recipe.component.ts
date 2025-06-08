import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormArray,
  Validators,
  ReactiveFormsModule,
  FormControl,
  AbstractControl
} from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AdminService, NamedEntity } from '../service/admin.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';


@Component({
  selector: 'app-add-recipe',
  templateUrl: './add-recipe.component.html',
  styleUrls: ['./add-recipe.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class AddRecipeComponent {
  recipeForm: FormGroup;
  
  tagSearchResults: { [index: number]: NamedEntity[] } = {};
  ingredientSearchResults: { [index: number]: NamedEntity[] } = {};
  unitSearchResults: { [index: number]: NamedEntity[] } = {};

  activeTagInput: number | null = null;
  activeIngredientInput: number | null = null;
  activeUnitInput: number | null = null;
  
  adminService: AdminService = inject(AdminService)

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.recipeForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      time: [0, [Validators.required, Validators.min(1)]],
      portions: [0, [Validators.required, Validators.min(1)]],
      difficulty: ['', [Validators.required]],
      tags: this.fb.array([]),
      recipeSteps: this.fb.array([]),
      ingredientUnits: this.fb.array([])
    });
  }

  selectTag(index: number, tagValue: NamedEntity) {
    const tagControl = this.tags.at(index).get('name');
    if (tagControl) {
      tagControl.setValue(tagValue.name);
      this.tagSearchResults[index] = []; 
      this.activeTagInput = null;
    }
  }

  onTagInputFocus(index: number) {
    this.activeTagInput = index;
  }

  onTagInputBlur(index: number) {
    setTimeout(() => {
      if (this.activeTagInput === index) {
        this.activeTagInput = null;
      }
    }, 200);
  }

  get tags(): FormArray {
    return this.recipeForm.get('tags') as FormArray;
  }

  addTag() {
    const newIndex = this.tags.length; 
    this.tags.push(this.fb.group({ name: ['', Validators.required] }));
    this.setupTagSearch(newIndex);
  }

  private setupTagSearch(index: number) {
    const tagControl = this.tags.at(index).get('name');
    
    if (!tagControl) return;

    tagControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(searchTerm => this.adminService.searchTags(searchTerm)),
      )
      .subscribe(results => {
        this.tagSearchResults[index] = results;
      });
  }

  removeTag(index: number) {
    this.tags.removeAt(index);
  }

  onSubmit() {
    if (this.recipeForm.valid) {
      this.http.post('http://localhost:8080/api/recipe', this.recipeForm.value).subscribe({
        next: () => alert('Przepis dodany!'),
        error: () => alert('Wystąpił błąd podczas dodawania przepisu.')
      });
    }
  }

  getFormControl(control: AbstractControl | null): FormControl {
    if (!control) {
      throw new Error('Control is null');
    }
    return control as FormControl;
  }

  protected readonly FormControl = FormControl;
}
