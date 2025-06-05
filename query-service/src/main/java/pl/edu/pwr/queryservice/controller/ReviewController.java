package pl.edu.pwr.queryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pwr.queryservice.dto.review.ReviewDTO;
import pl.edu.pwr.queryservice.service.ReviewService;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes","unchecked"})
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(params = "recipeId")
    public ResponseEntity<PagedModel<ReviewDTO>> getReviewsByRecipe(@RequestParam Long recipeId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int limit,
                                                                    PagedResourcesAssembler assembler) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ReviewDTO> reviews = reviewService.getReviewsByRecipeId(recipeId, pageable);
        return ResponseEntity.ok(assembler.toModel(reviews));
    }

    @GetMapping(params = "username")
    public ResponseEntity<PagedModel<ReviewDTO>> getReviewsByUser(@RequestParam String username,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int limit,
                                                                  PagedResourcesAssembler assembler) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ReviewDTO> reviews = reviewService.getReviewsByUsername(username, pageable);
        return ResponseEntity.ok(assembler.toModel(reviews));
    }
}
