package pl.edu.pwr.commandservice.dto;

import pl.edu.pwr.commandservice.enums.DifficultyLevel;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeDTO(
        Long id,
        String title,
        Integer authorId,
        String description,
        Integer time,
        Integer portions,
        DifficultyLevel difficulty,
        Double averageRating,
        List<String> tags,
        List<String> steps,
        List<String> ingredients,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
