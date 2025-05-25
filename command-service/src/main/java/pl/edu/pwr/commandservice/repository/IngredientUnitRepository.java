package pl.edu.pwr.commandservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pwr.commandservice.entity.ingredient.IngredientUnit;

public interface IngredientUnitRepository extends JpaRepository<IngredientUnit, Long> {
}
