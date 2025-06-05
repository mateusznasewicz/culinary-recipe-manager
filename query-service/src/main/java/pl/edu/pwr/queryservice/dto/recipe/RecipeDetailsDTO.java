package pl.edu.pwr.queryservice.dto.recipe;

import pl.edu.pwr.queryservice.entity.DifficultyLevel;

import java.util.List;

public record RecipeDetailsDTO (
        String author,
        String title,
        String description,
        Integer time,
        Integer portions,
        DifficultyLevel difficulty,
        List<String> steps,
        List<String> tags,
        List<String> ingredients
){}
