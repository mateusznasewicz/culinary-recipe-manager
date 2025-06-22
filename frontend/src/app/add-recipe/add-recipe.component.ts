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
import { CommonModule } from '@angular/common';
import { AdminService, NamedEntity } from '../service/admin.service';
import { AuthService } from '../service/auth.service';
import { RecipeService } from '../service/recipe.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

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
  isSubmitting = false;

  adminService: AdminService = inject(AdminService);
  authService: AuthService = inject(AuthService);
  recipeService: RecipeService = inject(RecipeService);

  constructor(private fb: FormBuilder) {
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
    this.addStep();
    this.addIngredientUnit();
  }

  get tags(): FormArray {
    return this.recipeForm.get('tags') as FormArray;
  }

  get recipeSteps(): FormArray {
    return this.recipeForm.get('recipeSteps') as FormArray;
  }

  get ingredientUnits(): FormArray {
    return this.recipeForm.get('ingredientUnits') as FormArray;
  }

  addTag() {
    const newIndex = this.tags.length;
    this.tags.push(this.fb.group({
      id: [null, Validators.required],
      name: ['', Validators.required]
    }));
    this.setupTagSearch(newIndex);
  }

  removeTag(index: number) {
    this.tags.removeAt(index);
    delete this.tagSearchResults[index];
  }

  addStep() {
    this.recipeSteps.push(this.fb.group({
      stepNumber: [this.recipeSteps.length + 1, [Validators.required, Validators.min(1)]],
      description: ['', [Validators.required, Validators.minLength(10)]]
    }));
  }

  removeStep(index: number) {
    this.recipeSteps.removeAt(index);
    this.recipeSteps.controls.forEach((control, i) => {
      control.get('stepNumber')?.setValue(i + 1);
    });
  }

  addIngredientUnit() {
    const newIndex = this.ingredientUnits.length;
    this.ingredientUnits.push(this.fb.group({
      ingredientId: [null, Validators.required],
      ingredientName: ['', Validators.required],
      quantity: [0, [Validators.required, Validators.min(0.01)]],
      unitId: [null, Validators.required],
      unitName: ['', Validators.required]
    }));
    this.setupIngredientSearch(newIndex);
    this.setupUnitSearch(newIndex);
  }

  removeIngredientUnit(index: number) {
    this.ingredientUnits.removeAt(index);
    delete this.ingredientSearchResults[index];
    delete this.unitSearchResults[index];
  }

  private setupTagSearch(index: number) {
    const tagControl = this.tags.at(index).get('name');
    if (tagControl) {
      tagControl.valueChanges
        .pipe(
          debounceTime(300),
          distinctUntilChanged(),
          switchMap(searchTerm => this.adminService.searchTags(searchTerm))
        )
        .subscribe(results => {
          this.tagSearchResults[index] = results;
        });
    }
  }

  private setupIngredientSearch(index: number) {
    const ingredientControl = this.ingredientUnits.at(index).get('ingredientName');
    if (ingredientControl) {
      ingredientControl.valueChanges
        .pipe(
          debounceTime(300),
          distinctUntilChanged(),
          switchMap(searchTerm => this.adminService.searchIngredients(searchTerm))
        )
        .subscribe(results => {
          this.ingredientSearchResults[index] = results;
        });
    }
  }

  private setupUnitSearch(index: number) {
    const unitControl = this.ingredientUnits.at(index).get('unitName');
    if (unitControl) {
      unitControl.valueChanges
        .pipe(
          debounceTime(300),
          distinctUntilChanged(),
          switchMap(searchTerm => this.adminService.searchUnits(searchTerm))
        )
        .subscribe(results => {
          this.unitSearchResults[index] = results;
        });
    }
  }

  selectTag(index: number, tagValue: NamedEntity) {
    const tagGroup = this.tags.at(index);
    tagGroup.get('id')?.setValue(tagValue.id);
    tagGroup.get('name')?.setValue(tagValue.name);
    this.tagSearchResults[index] = [];
    this.activeTagInput = null;
  }

  selectIngredient(index: number, ingredientValue: NamedEntity) {
    const ingredientGroup = this.ingredientUnits.at(index);
    ingredientGroup.get('ingredientId')?.setValue(ingredientValue.id);
    ingredientGroup.get('ingredientName')?.setValue(ingredientValue.name);
    this.ingredientSearchResults[index] = [];
    this.activeIngredientInput = null;
  }

  selectUnit(index: number, unitValue: NamedEntity) {
    const ingredientGroup = this.ingredientUnits.at(index);
    ingredientGroup.get('unitId')?.setValue(unitValue.id);
    ingredientGroup.get('unitName')?.setValue(unitValue.name);
    this.unitSearchResults[index] = [];
    this.activeUnitInput = null;
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

  onIngredientInputFocus(index: number) {
    this.activeIngredientInput = index;
  }

  onIngredientInputBlur(index: number) {
    setTimeout(() => {
      if (this.activeIngredientInput === index) {
        this.activeIngredientInput = null;
      }
    }, 200);
  }

  onUnitInputFocus(index: number) {
    this.activeUnitInput = index;
  }

  onUnitInputBlur(index: number) {
    setTimeout(() => {
      if (this.activeUnitInput === index) {
        this.activeUnitInput = null;
      }
    }, 200);
  }

  onSubmit() {
    if (!this.recipeForm.valid) {
      this.recipeForm.markAllAsTouched();
      alert('Formularz zawiera błędy. Upewnij się, że wszystkie pola są wypełnione, w tym tagi, składniki i jednostki.');
      return;
    }

    if (this.isSubmitting) {
      return;
    }

    if (!this.authService.isLoggedIn()) {
      alert('Musisz być zalogowany, aby dodać przepis.');
      return;
    }

    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      alert('Błąd: Nie można pobrać ID użytkownika z tokenu.');
      return;
    }

    this.isSubmitting = true;

    const recipeData = {
      authorId: userId,
      title: this.recipeForm.get('title')?.value,
      description: this.recipeForm.get('description')?.value,
      time: Number(this.recipeForm.get('time')?.value),
      portions: Number(this.recipeForm.get('portions')?.value),
      difficulty: this.recipeForm.get('difficulty')?.value,
      recipeSteps: this.recipeSteps.controls.map((control, index) => ({
        stepNumber: index + 1,
        description: control.get('description')?.value
      })),
      tags: this.tags.controls.map(control => ({
        id: control.get('id')?.value,
        name: control.get('name')?.value
      })),
      ingredientUnits: this.ingredientUnits.controls.map(control => ({
        ingredientId: control.get('ingredientId')?.value,
        unitId: control.get('unitId')?.value,
        quantity: Number(control.get('quantity')?.value)
      }))
    };

    console.log('Sending recipe data:', recipeData);

    this.recipeService.addRecipe(recipeData).subscribe({
      next: (response) => {
        console.log('Recipe added successfully:', response);
        this.isSubmitting = false;
        this.recipeForm.reset();
        this.tags.clear();
        this.recipeSteps.clear();
        this.ingredientUnits.clear();
        this.addStep();
        this.addIngredientUnit();
        alert('Przepis dodany pomyślnie!');
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error adding recipe:', err);
        this.isSubmitting = false;
        let errorMsg = 'Wystąpił błąd podczas dodawania przepisu.';
        if (err.status === 201) {
          console.log('Received 201 Created, assuming recipe was added');
          this.recipeForm.reset();
          this.tags.clear();
          this.recipeSteps.clear();
          this.ingredientUnits.clear();
          this.addStep();
          this.addIngredientUnit();
          alert('Przepis dodany pomyślnie, mimo błędu parsowania odpowiedzi.');
        } else {
          if (err.status) {
            errorMsg += ` Status: ${err.status}`;
          }
          if (err.error) {
            try {
              errorMsg += ` Szczegóły: ${JSON.stringify(err.error)}`;
            } catch {
              errorMsg += ` Szczegóły: ${err.error.text || err.error || 'Brak szczegółów'}`;
            }
          }
          console.log('Full error response:', {
            status: err.status,
            statusText: err.statusText,
            error: err.error,
            headers: err.headers
          });
          alert(errorMsg);
        }
      }
    });
  }

  getFormControl(control: AbstractControl | null): FormControl {
    if (!control) {
      throw new Error('Control is null');
    }
    return control as FormControl;
  }

  protected readonly FormControl = FormControl;
}
