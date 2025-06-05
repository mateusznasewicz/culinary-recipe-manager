package pl.edu.pwr.queryservice.dto.review;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pwr.queryservice.entity.Review;
import pl.edu.pwr.queryservice.repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {

    @Autowired
    private UserRepository userRepository;

    @Mapping(target = "author", source = "id.userId", qualifiedByName = "mapAuthor")
    public abstract ReviewDTO toDto(Review entity);

    @Named("mapAuthor")
    protected String mapAuthor(Long id) {
        return userRepository.findUsernameById(id).orElse("");
    }
}
