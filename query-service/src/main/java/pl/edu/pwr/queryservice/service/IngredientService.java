package pl.edu.pwr.queryservice.service;

import org.springframework.stereotype.Service;
import pl.edu.pwr.queryservice.entity.Ingredient;
import pl.edu.pwr.queryservice.repository.IngredientRepository;

@Service
public class IngredientService extends NamedEntityService<Ingredient> {
    public IngredientService(IngredientRepository repository) {
        super(repository);
    }
}
