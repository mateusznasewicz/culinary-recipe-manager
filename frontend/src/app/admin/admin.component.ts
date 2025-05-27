// src/app/admin/admin-panel.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService } from '../service/admin.service';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent {
  activeTab: 'ingredients' | 'units' | 'tags' = 'ingredients';
  ingredientForm: FormGroup;
  unitForm: FormGroup;
  tagForm: FormGroup;

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

  switchTab(tab: 'ingredients' | 'units' | 'tags') {
    this.activeTab = tab;
  }

  addIngredient() {
    if (this.ingredientForm.valid) {
      this.adminService.addIngredient(this.ingredientForm.value).subscribe(() => {
        alert('Składnik dodany!');
        this.ingredientForm.reset();
      });
    }
  }

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
}
