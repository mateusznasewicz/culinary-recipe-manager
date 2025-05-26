package pl.edu.pwr.queryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes_read")
@Data
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Size(max = 255)
    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "description")
    private String description;

    @NotNull
    @Min(1)
    @Max(599)
    @Column(name = "time", nullable = false)
    private Integer time;

    @NotNull
    @Min(1)
    @Max(20)
    @Column(name = "portions", nullable = false)
    private Integer portions;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private DifficultyLevel difficulty;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Digits(integer = 2, fraction = 1)
    @Column(name = "average_rating")
    private BigDecimal averageRating;

    @NotNull
    @ElementCollection
    @Column(name = "tags")
    private List<String> tags;

    @NotNull
    @ElementCollection
    @Column(name = "steps")
    private List<String> steps;

    @NotNull
    @ElementCollection
    @Column(name = "ingredients")
    private List<String> ingredients;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
