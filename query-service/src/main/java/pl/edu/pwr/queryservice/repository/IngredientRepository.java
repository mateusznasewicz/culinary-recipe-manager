package pl.edu.pwr.queryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pwr.queryservice.entity.Ingredient;

import java.util.Set;

public interface IngredientRepository extends NamedEntityRepository<Ingredient> {
    @Query(value = """
        SELECT * FROM ingredients_write 
        WHERE name % :q
        ORDER BY similarity(name, :q) DESC
        LIMIT :lim 
        """,
            nativeQuery = true
    )
    Set<Ingredient> findByQuery(@Param("q") String q, @Param("lim") int limit);
}
