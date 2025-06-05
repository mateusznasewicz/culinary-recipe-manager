package pl.edu.pwr.queryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pwr.queryservice.entity.Tag;

import java.util.Set;

public interface TagRepository extends NamedEntityRepository<Tag> {
    @Query(value = """
        SELECT * FROM tags_write 
        WHERE name % :q
        ORDER BY similarity(name, :q) DESC
        LIMIT :lim 
        """,
            nativeQuery = true
    )
    Set<Tag> findByQuery(@Param("q") String q, @Param("lim") int limit);
}
