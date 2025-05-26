package pl.edu.pwr.commandservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.edu.pwr.commandservice.dto.recipe.RecipeDTO;
import pl.edu.pwr.commandservice.dto.recipe.RecipeMapper;
import pl.edu.pwr.commandservice.entity.Tag;
import pl.edu.pwr.commandservice.entity.ingredient.IngredientUnit;
import pl.edu.pwr.commandservice.entity.recipe.Recipe;
import pl.edu.pwr.commandservice.entity.recipe.RecipeStep;
import pl.edu.pwr.commandservice.messaging.MessagePublisher;
import pl.edu.pwr.commandservice.repository.IngredientUnitRepository;
import pl.edu.pwr.commandservice.repository.RecipeRepository;
import pl.edu.pwr.commandservice.repository.admin.TagRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientUnitRepository ingredientUnitRepository;
    private final TagRepository tagRepository;
    private final MessagePublisher messagePublisher;
    private final RecipeMapper recipeMapper;

    //TODO nie zapisywac tych samych (quantity,ingredient,unit)
    public void save(RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.toEntity(recipeDTO);

        Set<RecipeStep> recipeSteps = recipe.getRecipeSteps();
        recipeSteps.forEach(recipeStep -> recipeStep.setRecipe(recipe));

        Set<IngredientUnit> ingredientUnits = recipe.getIngredientUnits();
        ingredientUnitRepository.saveAll(ingredientUnits);

        Set<String> tagsNames = recipe.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
        Set<Tag> tags = tagRepository.findAllByNameIn(tagsNames);
        recipe.setTags(tags);

        if(tagsNames.size() != tags.size()) {
            throw new DataIntegrityViolationException("Some tag IDs are invalid");
        }

        recipeRepository.save(recipe);
//        messagePublisher.sendRecipeMessage(recipeMapper.toEvent(recipeDTO));
    }
}
