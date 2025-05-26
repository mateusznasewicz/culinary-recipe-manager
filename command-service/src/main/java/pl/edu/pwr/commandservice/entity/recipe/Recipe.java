package pl.edu.pwr.commandservice.entity.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import pl.edu.pwr.commandservice.entity.Tag;
import pl.edu.pwr.commandservice.entity.ingredient.IngredientUnit;
import pl.edu.pwr.commandservice.enums.DifficultyLevel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipes_write")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer authorId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer time;

    @Column(nullable = false)
    private Integer portions;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "difficulty_level", nullable = false)
    @ColumnTransformer(write="?::difficulty_level")
    private DifficultyLevel difficulty;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeStep> recipeSteps = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "recipes_tags_write",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "recipe_ingredients_write",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_unit_id")
    )
    private Set<IngredientUnit> ingredientUnits = new HashSet<>();
}
