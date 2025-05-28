import { Component } from '@angular/core';
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


@Component({
  selector: 'app-add-recipe',
  templateUrl: './add-recipe.component.html',
  styleUrls: ['./add-recipe.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class AddRecipeComponent {
  recipeForm: FormGroup;
  availableTags = ['Italian', 'Meat', 'Vegetarian', 'Dessert'];
  availableIngredients = [
    { id: 1, name: 'Mąka' },
    { id: 2, name: 'Cukier' },
    { id: 3, name: 'Jajka' }
  ];
  availableUnits = [
    { id: 1, name: 'Gram' },
    { id: 2, name: 'Mililitr' },
    { id: 3, name: 'Sztuka' }
  ]; // zeby cos zaprezentowac

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
    this.tags.push(this.fb.group({ name: ['', Validators.required] }));
  }

  addStep() {
    this.recipeSteps.push(this.fb.group({
      stepNumber: [this.recipeSteps.length + 1, Validators.required],
      description: ['', Validators.required]
    }));
  }

  addIngredientUnit() {
    this.ingredientUnits.push(this.fb.group({
      ingredientId: [null, Validators.required],
      unitId: [null, Validators.required],
      quantity: [0, [Validators.required, Validators.min(0.1)]]
    }));
  }

  removeTag(index: number) {
    this.tags.removeAt(index);
  }

  removeStep(index: number) {
    this.recipeSteps.removeAt(index);
  }

  removeIngredientUnit(index: number) {
    this.ingredientUnits.removeAt(index);
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
