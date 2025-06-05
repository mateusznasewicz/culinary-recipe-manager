package pl.edu.pwr.queryservice.dto;

import pl.edu.pwr.queryservice.entity.DifficultyLevel;

import java.util.List;

public record RecipeDTO(
    String author,
    String title,
    Integer time,
    Integer portions,
    DifficultyLevel difficulty,
    List<String> tags
) { }
