package pl.edu.pwr.queryservice.service;

import org.springframework.stereotype.Service;
import pl.edu.pwr.queryservice.entity.Tag;
import pl.edu.pwr.queryservice.repository.TagRepository;

@Service
public class TagService extends NamedEntityService<Tag> {
    public TagService(TagRepository repository) {
        super(repository);
    }
}
