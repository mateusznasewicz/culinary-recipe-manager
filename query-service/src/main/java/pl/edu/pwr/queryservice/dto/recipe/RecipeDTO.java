package pl.edu.pwr.queryservice.dto.recipe;

import pl.edu.pwr.queryservice.entity.DifficultyLevel;

import java.util.List;

public record RecipeDTO(
    Long id,
    String author,
    String title,
    Integer time,
    Integer portions,
    DifficultyLevel difficulty,
    List<String> tags
) { }
