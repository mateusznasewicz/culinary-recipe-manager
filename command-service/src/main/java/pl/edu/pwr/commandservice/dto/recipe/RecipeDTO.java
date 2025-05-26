package pl.edu.pwr.commandservice.dto.recipe;

import pl.edu.pwr.commandservice.entity.Tag;
import pl.edu.pwr.commandservice.entity.ingredient.IngredientUnit;
import pl.edu.pwr.commandservice.entity.recipe.RecipeStep;
import pl.edu.pwr.commandservice.enums.DifficultyLevel;
import java.util.Set;

public record RecipeDTO (
    Long authorId,
    String title,
    String description,
    Integer time,
    Integer portions,
    DifficultyLevel difficulty,
    Set<RecipeStep> recipeSteps,
    Set<Tag> tags,
    Set<IngredientUnit> ingredientUnits
){}
