package pl.edu.pwr.commandservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.commandservice.dto.ReviewDTO;
import pl.edu.pwr.commandservice.service.ReviewService;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> addReview(
            @RequestBody ReviewDTO review,
            @RequestHeader("X-User-Id") String xUserId) {

        if (!xUserId.equals(String.valueOf(review.userId()))) {
            throw new DataIntegrityViolationException("");
        }

        reviewService.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review added");
    }
}
