package pl.edu.pwr.queryservice.repository.namedEntityRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pl.edu.pwr.queryservice.entity.Ingredient;

@Repository
public class IngredientRepository extends NamedEntityRepositoryImpl<Ingredient>{
    public IngredientRepository(EntityManager em) {
        super(em,"ingredients_write", Ingredient.class);
    }
}
