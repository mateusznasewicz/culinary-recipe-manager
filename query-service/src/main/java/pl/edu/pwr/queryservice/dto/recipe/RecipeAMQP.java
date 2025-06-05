package pl.edu.pwr.queryservice.dto.recipe;

import pl.edu.pwr.queryservice.entity.DifficultyLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RecipeAMQP(
        Long id,
        String title,
        Integer authorId,
        String description,
        Integer time,
        Integer portions,
        DifficultyLevel difficulty,
        BigDecimal averageRating,
        List<String> tags,
        List<String> steps,
        List<String> ingredients,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
