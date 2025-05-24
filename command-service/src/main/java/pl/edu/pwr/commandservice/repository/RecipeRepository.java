package pl.edu.pwr.commandservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pwr.commandservice.entity.recipe.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
