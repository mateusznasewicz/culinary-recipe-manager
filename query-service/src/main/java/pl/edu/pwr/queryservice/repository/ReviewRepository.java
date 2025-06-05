package pl.edu.pwr.queryservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pwr.queryservice.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByIdUserId(Long userId, Pageable pageable);
    Page<Review> findAllByIdRecipeId(Long recipeId, Pageable pageable);
}
