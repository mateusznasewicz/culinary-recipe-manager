import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService, NamedEntity } from '../service/admin.service';
import { debounceTime, distinctUntilChanged, Observable, switchMap, of } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class AdminPanelComponent {
  activeTab: 'ingredients' | 'units' | 'tags' = 'ingredients';
  ingredientForm: FormGroup;
  unitForm: FormGroup;
  tagForm: FormGroup;
  ingredients: NamedEntity[] = [];
  units: NamedEntity[] = [];
  tags: NamedEntity[] = [];
  searchResults: NamedEntity[] = [];
  searchInputValue = '';

  constructor(private fb: FormBuilder, private adminService: AdminService) {
    this.ingredientForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]]
    });
    this.unitForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]]
    });
    this.tagForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]]
    });

    this.setupSearch('name', this.ingredientForm, 'ingredients');
    this.setupSearch('name', this.unitForm, 'units');
    this.setupSearch('name', this.tagForm, 'tags');
  }

  private setupSearch(controlName: string, form: FormGroup, type: 'ingredients' | 'units' | 'tags') {
    form.get(controlName)?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(searchTerm => {
        if (searchTerm.length < 3) {
          this.searchResults = [];
          return [];
        }
        return this.getSearchObservable(type, searchTerm);
      })
    ).subscribe(results => {
      this.searchResults = results;
      this.updateEntityList(type, results);
    });
  }

  private getSearchObservable(type: 'ingredients' | 'units' | 'tags', searchTerm: string): Observable<NamedEntity[]> {
    switch (type) {
      case 'ingredients': return this.adminService.searchIngredients(searchTerm);
      case 'units': return this.adminService.searchUnits(searchTerm);
      case 'tags': return this.adminService.searchTags(searchTerm);
      default: return of([]);    }
  }

  private updateEntityList(type: 'ingredients' | 'units' | 'tags', results: NamedEntity[]) {
    switch (type) {
      case 'ingredients': this.ingredients = results; break;
      case 'units': this.units = results; break;
      case 'tags': this.tags = results; break;
    }
  }

  switchTab(tab: 'ingredients' | 'units' | 'tags') {
    this.activeTab = tab;
    this.searchResults = [];
    this.searchInputValue = '';
    this.ingredientForm.reset();
    this.unitForm.reset();
    this.tagForm.reset();
  }

  onSearchInput(event: Event, type: 'ingredients' | 'units' | 'tags') {
    const input = event.target as HTMLInputElement;
    this.searchInputValue = input.value;
  }

  selectSearchResult(result: NamedEntity, type: 'ingredients' | 'units' | 'tags') {
    const form = this.getFormByType(type);
    form.patchValue({ name: result.name });
    this.searchResults = [];
    this.searchInputValue = result.name;
    const input = document.querySelector(`input[placeholder="Wpisz ${type === 'ingredients' ? 'składnik' : type === 'units' ? 'jednostkę' : 'tag'}..."]`) as HTMLInputElement;
    if (input) input.value = result.name;
  }

  private getFormByType(type: 'ingredients' | 'units' | 'tags'): FormGroup {
    switch (type) {
      case 'ingredients': return this.ingredientForm;
      case 'units': return this.unitForm;
      case 'tags': return this.tagForm;
    }
  }

  addIngredient() {
    if (this.ingredientForm.valid) {
      this.adminService.addIngredient(this.ingredientForm.value).subscribe({
        next: () => {
          alert('Składnik dodany!');
          this.ingredientForm.reset();
          this.searchResults = [];
          this.searchInputValue = '';
        },
        error: err => {
          console.error('Add ingredient error:', err);
          alert('Błąd podczas dodawania składnika.');
        }
      });
    }
  }

  addUnit() {
    if (this.unitForm.valid) {
      this.adminService.addUnit(this.unitForm.value).subscribe({
        next: () => {
          alert('Jednostka dodana!');
          this.unitForm.reset();
          this.searchResults = [];
          this.searchInputValue = '';
        },
        error: err => {
          console.error('Add unit error:', err);
          alert('Błąd podczas dodawania jednostki.');
        }
      });
    }
  }

  addTag() {
    if (this.tagForm.valid) {
      this.adminService.addTag(this.tagForm.value).subscribe({
        next: () => {
          alert('Tag dodany!');
          this.tagForm.reset();
          this.searchResults = [];
          this.searchInputValue = '';
        },
        error: err => {
          console.error('Add tag error:', err);
          alert('Błąd podczas dodawania tagu.');
        }
      });
    }
  }

  // editItem(type: 'ingredient' | 'unit' | 'tag', item: NamedEntity) {
  //   const newName = prompt(`Edytuj nazwę ${type === 'ingredient' ? 'składnika' : type === 'unit' ? 'jednostki' : 'tagu'}:`, item.name);
  //   if (newName) {
  //     const updateData = { id: item.id, name: newName };
  //     const updateMethod = type === 'ingredient' ? this.adminService.updateIngredient(updateData)
  //       : type === 'unit' ? this.adminService.updateUnit(updateData)
  //         : this.adminService.updateTag(updateData);
  //
  //     updateMethod.subscribe({
  //       next: () => {
  //         alert(`${type.charAt(0).toUpperCase() + type.slice(1)} zaktualizowany!`);
  //         this.updateEntityList(type, [...this.getEntityList(type), { ...item, name: newName }]);
  //       },
  //       error: () => {
  //         alert('Funkcja edycji jeszcze niedostępna.');
  //       }
  //     });
  //   }
  // }
  //
  // deleteItem(type: 'ingredient' | 'unit' | 'tag', id: number) {
  //   if (confirm(`Czy na pewno chcesz usunąć ${type === 'ingredient' ? 'składnik' : type === 'unit' ? 'jednostkę' : 'tag'}?`)) {
  //     const deleteMethod = type === 'ingredient' ? this.adminService.deleteIngredient(id)
  //       : type === 'unit' ? this.adminService.deleteUnit(id)
  //         : this.adminService.deleteTag(id);
  //
  //     deleteMethod.subscribe({
  //       next: () => {
  //         alert(`${type.charAt(0).toUpperCase() + type.slice(1)} usunięty!`);
  //         this.updateEntityList(type, this.getEntityList(type).filter(item => item.id !== id));
  //       },
  //       error: () => {
  //         alert('Funkcja usuwania jeszcze niedostępna.');
  //       }
  //     });
  //   }
  // }

  private getEntityList(type: 'ingredients' | 'units' | 'tags'): NamedEntity[] {
    switch (type) {
      case 'ingredients': return this.ingredients;
      case 'units': return this.units;
      case 'tags': return this.tags;
    }
  }
}
