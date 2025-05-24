package pl.edu.pwr.commandservice.entity.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_steps_write")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(nullable = false)
    private Integer step_number;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
}
