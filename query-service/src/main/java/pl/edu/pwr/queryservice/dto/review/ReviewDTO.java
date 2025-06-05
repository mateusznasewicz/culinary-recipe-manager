package pl.edu.pwr.queryservice.dto.review;

import java.math.BigDecimal;

public record ReviewDTO (
    String author,
    BigDecimal rating,
    String comment
) {}
