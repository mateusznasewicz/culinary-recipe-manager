package pl.edu.pwr.commandservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pwr.commandservice.entity.review.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByIdUserIdAndIdRecipeId(Long userId, Long recipeId);

}
