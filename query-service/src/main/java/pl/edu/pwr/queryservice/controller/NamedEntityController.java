package pl.edu.pwr.queryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pwr.queryservice.entity.Ingredient;
import pl.edu.pwr.queryservice.entity.Tag;
import pl.edu.pwr.queryservice.entity.Unit;
import pl.edu.pwr.queryservice.service.namedEntityService.IngredientService;
import pl.edu.pwr.queryservice.service.namedEntityService.TagService;
import pl.edu.pwr.queryservice.service.namedEntityService.UnitService;

import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NamedEntityController {

    private final TagService tagService;
    private final UnitService unitService;
    private final IngredientService ingredientService;

    @GetMapping("/tag")
    public ResponseEntity<Set<Tag>> getTags(@RequestParam String q) {
        return ResponseEntity.ok(tagService.findByQuery(q));
    }

    @GetMapping("/ingredient")
    public ResponseEntity<Set<Ingredient>> getIngredients(@RequestParam String q) {
        return ResponseEntity.ok(ingredientService.findByQuery(q));
    }

    @GetMapping("/unit")
    public ResponseEntity<Set<Unit>> getUnits(@RequestParam String q) {
        return ResponseEntity.ok(unitService.findByQuery(q));
    }
}
