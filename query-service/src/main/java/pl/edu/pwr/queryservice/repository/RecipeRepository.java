package pl.edu.pwr.queryservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pwr.queryservice.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query(value = "SELECT * FROM recipes_read WHERE tags @> CAST(:tags AS text[])", nativeQuery = true)
    Page<Recipe> findByTags(@Param("tags") String tags, Pageable pageable);

    @Query(value = """
            SELECT id,title,author,description,time,portions,difficulty,average_rating,tags,steps,ingredients,created_at,updated_at FROM (
                SELECT *, similarity(title, :query) AS sim
                FROM recipes_read
            )
            WHERE sim > 0.2
            ORDER BY sim DESC
            """, nativeQuery = true)
    Page<Recipe> findByQuery(@Param("query") String query, Pageable pageable);
    Page<Recipe> findByAuthor(String author, Pageable pageable);
}
