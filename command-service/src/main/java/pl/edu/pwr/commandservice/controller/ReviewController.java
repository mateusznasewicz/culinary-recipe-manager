package pl.edu.pwr.commandservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pwr.commandservice.dto.ReviewDTO;
import pl.edu.pwr.commandservice.entity.recipe.Recipe;
import pl.edu.pwr.commandservice.entity.review.Review;
import pl.edu.pwr.commandservice.service.ReviewService;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody ReviewDTO review) {
        reviewService.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review added");
    }
}
