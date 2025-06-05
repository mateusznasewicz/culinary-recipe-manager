package pl.edu.pwr.queryservice.service.namedEntityService;

import org.springframework.stereotype.Service;
import pl.edu.pwr.queryservice.entity.Tag;
import pl.edu.pwr.queryservice.repository.namedEntityRepository.TagRepository;

@Service
public class TagService extends NamedEntityService<Tag> {
    public TagService(TagRepository repository) {
        super(repository);
    }
}
