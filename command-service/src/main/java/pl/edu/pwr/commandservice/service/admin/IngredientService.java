package pl.edu.pwr.commandservice.service.admin;

import org.springframework.stereotype.Service;
import pl.edu.pwr.commandservice.entity.ingredient.Ingredient;
import pl.edu.pwr.commandservice.repository.admin.IngredientRepository;

@Service
public class IngredientService extends AdminService<Ingredient> {
    public IngredientService(IngredientRepository repository) {
        super(repository, Ingredient.class);
    }
}
