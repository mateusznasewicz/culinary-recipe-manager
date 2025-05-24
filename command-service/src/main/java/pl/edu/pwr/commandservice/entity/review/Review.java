package pl.edu.pwr.commandservice.entity.review;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Review {

    @EmbeddedId
    private ReviewId id;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
