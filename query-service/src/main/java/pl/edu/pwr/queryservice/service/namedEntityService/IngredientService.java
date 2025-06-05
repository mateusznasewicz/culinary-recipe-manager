package pl.edu.pwr.queryservice.service.namedEntityService;

import org.springframework.stereotype.Service;
import pl.edu.pwr.queryservice.entity.Ingredient;
import pl.edu.pwr.queryservice.repository.namedEntityRepository.IngredientRepository;

@Service
public class IngredientService extends NamedEntityService<Ingredient> {
    public IngredientService(IngredientRepository repository) {
        super(repository);
    }
}
