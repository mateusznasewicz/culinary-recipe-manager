import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../service/admin.service';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  imports: [ReactiveFormsModule],
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {
  activeTab: 'ingredients' | 'units' | 'tags' = 'ingredients';
  ingredientForm: FormGroup;
  unitForm: FormGroup;
  tagForm: FormGroup;

  ingredients: any[] = [];
  units: any[] = [];
  tags: any[] = [];

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
  }

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.adminService.getIngredients().subscribe(data => this.ingredients = data);
    this.adminService.getUnits().subscribe(data => this.units = data);
    this.adminService.getTags().subscribe(data => this.tags = data);
  }

  switchTab(tab: 'ingredients' | 'units' | 'tags') {
    this.activeTab = tab;
  }

  addIngredient() {
    if (this.ingredientForm.valid) {
      this.adminService.addIngredient(this.ingredientForm.value).subscribe(() => {
        alert('Składnik dodany!');
        this.ingredientForm.reset();
        this.loadData();
      });
    }
  }

  addUnit() {
    if (this.unitForm.valid) {
      this.adminService.addUnit(this.unitForm.value).subscribe(() => {
        alert('Jednostka dodana!');
        this.unitForm.reset();
        this.loadData();
      });
    }
  }

  addTag() {
    if (this.tagForm.valid) {
      this.adminService.addTag(this.tagForm.value).subscribe(() => {
        alert('Tag dodany!');
        this.tagForm.reset();
        this.loadData();
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
        this.loadData();
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
        this.loadData();
      });
    }
  }
}
