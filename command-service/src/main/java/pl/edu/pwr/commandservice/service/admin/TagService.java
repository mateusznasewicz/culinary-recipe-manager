package pl.edu.pwr.commandservice.service.admin;

import org.springframework.stereotype.Service;
import pl.edu.pwr.commandservice.entity.Tag;
import pl.edu.pwr.commandservice.repository.admin.TagRepository;

@Service
public class TagService extends AdminService<Tag> {
    public TagService(TagRepository repository) {
        super(repository, Tag.class);
    }
}
