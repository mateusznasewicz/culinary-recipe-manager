package pl.edu.pwr.queryservice.dto.recipe;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pwr.queryservice.entity.Recipe;
import pl.edu.pwr.queryservice.repository.UserRepository;
import pl.edu.pwr.queryservice.repository.namedEntityRepository.IngredientRepository;
import pl.edu.pwr.queryservice.repository.namedEntityRepository.UnitRepository;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RecipeMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapAuthor")
    @Mapping(target = "averageRating", constant = "0.0")
    @Mapping(target = "ingredients", source = "ingredients", qualifiedByName = "mapIngredients")
    public abstract Recipe toEntity(RecipeAMQP recipeAMQP);

    public abstract RecipeDetailsDTO toDetailsDTO(Recipe recipe);

    public abstract RecipeDTO toDTO(Recipe recipe);

    @Named("mapAuthor")
    protected String mapAuthor(Long authorId) {
        return userRepository.findUsernameById(authorId).orElse("");
    }

    @Named("mapIngredients")
    protected List<String> mapIngredients(List<String> ingredients) {
        return ingredients.stream().map(item -> {
            String[] split = item.split(" ");
            String quantity = split[0];
            String unit = unitRepository.findNameById(Long.parseLong(split[1])).orElse("");
            String ingredient = ingredientRepository.findNameById(Long.parseLong(split[2])).orElse("");
            return quantity + " " + unit + " " + ingredient;
        }).toList();
    }
}
