package pl.edu.pwr.queryservice.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pl.edu.pwr.queryservice.entity.Tag;

@Repository
public class TagRepository extends NamedEntityRepositoryImpl<Tag>{
    public TagRepository(EntityManager em) {
        super(em,"tags_write", Tag.class);
    }
}
