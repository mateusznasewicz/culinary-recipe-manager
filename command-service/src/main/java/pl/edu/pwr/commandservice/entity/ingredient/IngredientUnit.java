package pl.edu.pwr.commandservice.entity.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredients_units_write")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal quantity;
}

