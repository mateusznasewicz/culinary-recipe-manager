package pl.edu.pwr.queryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.queryservice.dto.RecipeDTO;
import pl.edu.pwr.queryservice.dto.RecipeDetailsDTO;
import pl.edu.pwr.queryservice.service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes","unchecked"})
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailsDTO> getRecipeDetails(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    @GetMapping(params = "tags")
    public ResponseEntity<PagedModel<RecipeDTO>> getRecipesByTags(@RequestParam List<String> tags,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                PagedResourcesAssembler assembler) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeDTO> dtoPage = recipeService.findByTags(tags, pageable);
        return ResponseEntity.ok(assembler.toModel(dtoPage));
    }

    @GetMapping(params = "q")
    public ResponseEntity<PagedModel<RecipeDTO>> getRecipesByQuery(@RequestParam String q,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   PagedResourcesAssembler assembler){
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeDTO> dtoPage = recipeService.findByQuery(q, pageable);
        return ResponseEntity.ok(assembler.toModel(dtoPage));
    }
}
