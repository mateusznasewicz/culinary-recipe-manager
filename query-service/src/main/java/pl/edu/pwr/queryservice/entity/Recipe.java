package pl.edu.pwr.queryservice.entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "description")
    private String description;

    @Column(name = "time", nullable = false)
    private Integer time;

    @Column(name = "portions", nullable = false)
    private Integer portions;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "difficulty_level", nullable = false)
    @ColumnTransformer(write="?::difficulty_level")
    private DifficultyLevel difficulty;

    @Column(name = "average_rating")
    private BigDecimal averageRating;

    @Type(ListArrayType.class)
    @Column(name = "tags", columnDefinition = "text[]")
    private List<String> tags;

    @Type(ListArrayType.class)
    @Column(name = "steps", columnDefinition = "text[]")
    private List<String> steps;

    @Type(ListArrayType.class)
    @Column(name = "ingredients", columnDefinition = "text[]")
    private List<String> ingredients;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
