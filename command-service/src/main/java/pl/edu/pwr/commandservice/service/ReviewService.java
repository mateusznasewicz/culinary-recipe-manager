package pl.edu.pwr.commandservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.commandservice.dto.ReviewDTO;
import pl.edu.pwr.commandservice.entity.review.Review;
import pl.edu.pwr.commandservice.entity.review.ReviewId;
import pl.edu.pwr.commandservice.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void save(ReviewDTO review) {
        Review reviewEntity = Review.builder()
                .id(new ReviewId(review.userId(), review.recipeId()))
                .rating(review.rating())
                .comment(review.comment())
                .build();

        reviewRepository.save(reviewEntity);
    }
}
