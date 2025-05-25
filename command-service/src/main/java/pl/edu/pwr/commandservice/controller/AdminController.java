package pl.edu.pwr.commandservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.commandservice.entity.Tag;
import pl.edu.pwr.commandservice.entity.ingredient.Ingredient;
import pl.edu.pwr.commandservice.entity.ingredient.Unit;
import pl.edu.pwr.commandservice.service.admin.IngredientService;
import pl.edu.pwr.commandservice.service.admin.TagService;
import pl.edu.pwr.commandservice.service.admin.UnitService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final TagService tagService;
    private final UnitService unitService;
    private final IngredientService ingredientService;

    @PostMapping("/tag")
    public ResponseEntity<String> addTag(@RequestBody Tag tag) {
        tagService.save(tag);
        return ResponseEntity.ok("Tag added");
    }

    @PostMapping("/unit")
    public ResponseEntity<String> addUnit(@RequestBody Unit unit) {
        unitService.save(unit);
        return ResponseEntity.ok("Unit added");
    }

    @PostMapping("/ingredient")
    public ResponseEntity<String> addIngredient(@RequestBody Ingredient ingredient) {
        ingredientService.save(ingredient);
        return ResponseEntity.ok("Ingredient added");
    }

    @DeleteMapping("/tag")
    public ResponseEntity<String> deleteTag(@RequestParam Long id) {
        tagService.delete(id);
        return ResponseEntity.ok("Unit deleted");
    }

    @DeleteMapping("/unit")
    public ResponseEntity<String> deleteUnit(@RequestParam Long id) {
        unitService.delete(id);
        return ResponseEntity.ok("Unit deleted");
    }

    @DeleteMapping("/ingredient")
    public ResponseEntity<String> deleteIngredient(@RequestParam Long id) {
        ingredientService.delete(id);
        return ResponseEntity.ok("Unit deleted");
    }

    @PutMapping("/tag")
    public ResponseEntity<String> updateTag(@RequestParam Tag tag) {
        tagService.update(tag);
        return ResponseEntity.ok("Unit updated");
    }

    @PutMapping("/unit")
    public ResponseEntity<String> updateUnit(@RequestParam Unit unit) {
        unitService.update(unit);
        return ResponseEntity.ok("Unit updated");
    }

    @PutMapping("/ingredient")
    public ResponseEntity<String> updateIngredient(@RequestParam Ingredient ingredient) {
        ingredientService.update(ingredient);
        return ResponseEntity.ok("Unit updated");
    }
}
