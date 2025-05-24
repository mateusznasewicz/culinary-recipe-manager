import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RecipeService } from './service/recipe.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';
  data: string = '';
  private recipeService = inject(RecipeService);

  ngOnInit(): void {
    this.recipeService.getTest().subscribe({
      next: (res) => {
        console.log('Response:', res);
        this.data = res;
      },
      error: (err) => {
        console.error('Error:', err);
      }
    });
  }
}
