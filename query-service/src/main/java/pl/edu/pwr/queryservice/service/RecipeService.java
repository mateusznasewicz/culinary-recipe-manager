package pl.edu.pwr.queryservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pwr.queryservice.dto.RecipeDTO;
import pl.edu.pwr.queryservice.dto.RecipeDetailsDTO;
import pl.edu.pwr.queryservice.dto.RecipeMapper;
import pl.edu.pwr.queryservice.entity.Recipe;
import pl.edu.pwr.queryservice.repository.namedEntityRepository.RecipeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public void save(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public RecipeDetailsDTO findById(Long id) {
        Recipe recipeEntity = recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return recipeMapper.toDetailsDTO(recipeEntity);
    }

    public Page<RecipeDTO> findByTags(List<String> tags, Pageable pageable) {
        String pgArray = "{\"" + String.join("\",\"", tags) + "\"}";
        Page<Recipe> recipesPage = recipeRepository.findByTags(pgArray, pageable);
        return recipesPage.map(recipeMapper::toDTO);
    }

    public Page<RecipeDTO> findByQuery(String query, Pageable pageable) {
        Page<Recipe> recipesPage = recipeRepository.findByQuery(query, pageable);
        return recipesPage.map(recipeMapper::toDTO);
    }
}
