import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService, NamedEntity } from '../service/admin.service';
import { debounceTime, distinctUntilChanged, Observable, switchMap } from 'rxjs';
import { NgFor,  NgIf } from '@angular/common';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  imports: [ReactiveFormsModule,NgFor, NgIf],
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent {
  activeTab: 'ingredients' | 'units' | 'tags' = 'ingredients';
  ingredientForm: FormGroup;
  unitForm: FormGroup;
  tagForm: FormGroup;

  ingredients: NamedEntity[] = [];
  units: NamedEntity[] = [];
  tags: NamedEntity[] = [];

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

    this.setupSearch('name', this.ingredientForm,
      searchTerm => this.adminService.searchIngredients(searchTerm),
      results => {console.log("test")
        console.log(results)
        this.ingredients = results}
    );

    this.setupSearch('name', this.unitForm,
      searchTerm => this.adminService.searchUnits(searchTerm),
      results => {
        this.units = results
      }
    );

    this.setupSearch('name', this.tagForm,
      searchTerm => this.adminService.searchTags(searchTerm),
      results => this.tags = results
    );
  }

  private setupSearch(
    controlName: string,
    form: FormGroup,
    searchFunction: (term: string) => Observable<any[]>,
    resultHandler: (results: any[]) => void
  ) {
    form.get(controlName)?.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(searchTerm => searchFunction(searchTerm)),
      )
      .subscribe(results => resultHandler(results));
  }

  switchTab(tab: 'ingredients' | 'units' | 'tags') {
    this.activeTab = tab;
  }

  addIngredient() {
    if (this.ingredientForm.valid) {
      this.adminService.addIngredient(this.ingredientForm.value).subscribe({next: (response) => {console.log(response)}, error: err => {console.log(err)}})
    }}

  addUnit() {
    if (this.unitForm.valid) {
      this.adminService.addUnit(this.unitForm.value).subscribe(() => {
        alert('Jednostka dodana!');
        this.unitForm.reset();
      });
    }
  }

  addTag() {
    if (this.tagForm.valid) {
      this.adminService.addTag(this.tagForm.value).subscribe(() => {
        alert('Tag dodany!');
        this.tagForm.reset();
      });
    }
  }

  editItem(type: 'ingredient' | 'unit' | 'tag', item: any) {
    const newName = prompt(`Edytuj nazwę ${type}:`, item.name);
    if (newName) {
      const updateData = { id: item.id, name: newName };
      const updateMethod = type === 'ingredient' ? this.adminService.updateIngredient(updateData)
        : type === 'unit' ? this.adminService.updateUnit(updateData)
          : this.adminService.updateTag(updateData);

      updateMethod.subscribe(() => {
        alert(`${type.charAt(0).toUpperCase() + type.slice(1)} zaktualizowany!`);
      });
    }
  }

  deleteItem(type: 'ingredient' | 'unit' | 'tag', id: number) {
    if (confirm(`Czy na pewno chcesz usunąć ${type}?`)) {
      const deleteMethod = type === 'ingredient' ? this.adminService.deleteIngredient(id)
        : type === 'unit' ? this.adminService.deleteUnit(id)
          : this.adminService.deleteTag(id);

      deleteMethod.subscribe(() => {
        alert(`${type.charAt(0).toUpperCase() + type.slice(1)} usunięty!`);
      });
    }
  }
}
