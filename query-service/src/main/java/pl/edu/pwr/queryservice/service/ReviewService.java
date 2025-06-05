package pl.edu.pwr.queryservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pwr.queryservice.dto.review.ReviewDTO;
import pl.edu.pwr.queryservice.dto.review.ReviewMapper;
import pl.edu.pwr.queryservice.entity.Review;
import pl.edu.pwr.queryservice.repository.ReviewRepository;
import pl.edu.pwr.queryservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;

    public Page<ReviewDTO> getReviewsByUsername(String username, Pageable pageable) {
        Long id = userRepository.findIdByUsername(username).orElseThrow(EntityNotFoundException::new);
        Page<Review> reviews = reviewRepository.findAllByIdUserId(id, pageable);
        return reviews.map(reviewMapper::toDto);
    }

    public Page<ReviewDTO> getReviewsByRecipeId(Long recipeId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByIdRecipeId(recipeId, pageable);
        return reviews.map(reviewMapper::toDto);
    }
}
