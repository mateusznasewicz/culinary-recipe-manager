package pl.edu.pwr.commandservice.dto;

import java.math.BigDecimal;

public record ReviewDTO(
        Integer userId,
        Integer recipeId,
        BigDecimal rating,
        String comment
) {}

