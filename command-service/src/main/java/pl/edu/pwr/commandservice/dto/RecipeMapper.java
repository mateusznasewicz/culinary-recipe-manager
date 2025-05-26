package pl.edu.pwr.commandservice.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import pl.edu.pwr.commandservice.entity.Tag;
import pl.edu.pwr.commandservice.entity.ingredient.IngredientUnit;
import pl.edu.pwr.commandservice.entity.recipe.Recipe;
import pl.edu.pwr.commandservice.entity.recipe.RecipeStep;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    @Mapping(target = "steps", source = "recipeSteps", qualifiedByName = "mapSteps")
    @Mapping(target = "ingredients", source = "ingredientUnits", qualifiedByName = "mapIngredients")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTags")
    @Mapping(target = "averageRating", ignore = true)
    RecipeDTO toDTO(Recipe recipe);

    @Named("mapSteps")
    default List<String> mapSteps(Set<RecipeStep> steps) {
        return steps.stream()
                .sorted((s1, s2) -> s1.getStepNumber().compareTo(s2.getStepNumber()))
                .map(step -> step.getStepNumber() + ". " + step.getDescription())
                .collect(Collectors.toList());
    }

    @Named("mapIngredients")
    default List<String> mapIngredients(Set<IngredientUnit> ingredients) {
        return ingredients.stream()
                .map(ingredient -> {
                    String quantity = ingredient.getQuantity().stripTrailingZeros().toPlainString();
                    Long unitId = ingredient.getUnitId();
                    Long ingredientId = ingredient.getIngredientId();
                    return quantity + " " + unitId + " " + ingredientId;
                })
                .collect(Collectors.toList());
    }

    @Named("mapTags")
    default List<String> mapTags(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
