package pl.edu.pwr.queryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pwr.queryservice.entity.Unit;

import java.util.Set;

public interface UnitRepository extends NamedEntityRepository<Unit> {
    @Query(value = """
        SELECT * FROM units_write 
        WHERE name % :q
        ORDER BY similarity(name, :q) DESC
        LIMIT :lim 
        """,
        nativeQuery = true
    )
    Set<Unit> findByQuery(@Param("q") String q, @Param("lim") int limit);
}
