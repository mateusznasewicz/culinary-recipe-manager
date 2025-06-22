import { Routes } from '@angular/router';
import { AuthComponent } from './auth/auth.component';
import {AdminPanelComponent} from './admin-panel/admin-panel.component';
import {authGuard} from './guards/auth.guard';
import { AddRecipeComponent } from './add-recipe/add-recipe.component';
import { RecipeDetailsComponent } from './recipe-details/recipe-details.component';
import { RecipeSearchComponent } from './recipe-search/recipe-search.component';

export const routes: Routes = [
  { path: '', redirectTo: '/auth', pathMatch: 'full' },
  { path: 'auth', component: AuthComponent },
  { path: 'admin', component: AdminPanelComponent}, //, canActivate: [authGuard] },
  { path: 'add-recipe', component: AddRecipeComponent },
  { path: 'recipe/:id', component: RecipeDetailsComponent },
  { path: 'search', component: RecipeSearchComponent },
  { path: '**', redirectTo: '/auth' }
];
