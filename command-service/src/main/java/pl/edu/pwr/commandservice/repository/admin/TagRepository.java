package pl.edu.pwr.commandservice.repository.admin;

import org.springframework.stereotype.Repository;
import pl.edu.pwr.commandservice.entity.Tag;

@Repository
public interface TagRepository extends NamedEntityRepository<Tag> {
}
