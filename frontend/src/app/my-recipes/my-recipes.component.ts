import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { RecipeService } from '../service/recipe.service';
import {RouterLink} from '@angular/router';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-my-recipes',
  templateUrl: './my-recipes.component.html',
  styleUrls: ['./my-recipes.component.scss'],
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf
  ]
})
export class MyRecipesComponent implements OnInit {
  recipes: any[] = [];

  constructor(private authService: AuthService, private recipeService: RecipeService) {}

  ngOnInit() {
    const username = this.authService.getCurrentUsername();
    console.log(username)
    if (username) {
      console.log(username)
      this.recipeService.getRecipesByUsername(username).subscribe(recipes => {
        this.recipes = recipes._embedded.recipeDTOList;
        console.log('My Recipes:', this.recipes);
      });
    }
  }
}
