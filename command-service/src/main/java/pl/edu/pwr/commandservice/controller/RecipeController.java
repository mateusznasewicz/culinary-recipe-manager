package pl.edu.pwr.commandservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.commandservice.dto.recipe.RecipeDTO;
import pl.edu.pwr.commandservice.service.RecipeService;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private static final Logger logger = Logger.getLogger(RecipeController.class.getName());

    @PostMapping
    public ResponseEntity<String> addRecipe(@RequestBody RecipeDTO recipe) {
        recipeService.save(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body("Recipe added");
    }

}
