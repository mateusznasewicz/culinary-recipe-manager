package pl.edu.pwr.commandservice.repository.admin;

import org.springframework.stereotype.Repository;
import pl.edu.pwr.commandservice.entity.ingredient.Ingredient;

@Repository
public interface IngredientRepository extends NamedEntityRepository<Ingredient> {
}
