package pl.edu.pwr.commandservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pwr.commandservice.entity.review.Review;
import pl.edu.pwr.commandservice.service.ReviewService;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<String> review(@RequestBody Review review) {
        return null;
    }
}
