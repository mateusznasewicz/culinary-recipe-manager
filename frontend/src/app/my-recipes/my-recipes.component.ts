import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { RecipeService } from '../service/recipe.service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-my-recipes',
  templateUrl: './my-recipes.component.html',
  styleUrls: ['./my-recipes.component.scss'],
  standalone: true,
  imports: [
    RouterLink
  ]
})
export class MyRecipesComponent implements OnInit {
  recipes: any[] = [];

  constructor(private authService: AuthService, private recipeService: RecipeService) {}

  ngOnInit() {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.recipeService.getRecipesByUserId(userId).subscribe(recipes => {
        this.recipes = recipes;
      });
    }
  }
}
