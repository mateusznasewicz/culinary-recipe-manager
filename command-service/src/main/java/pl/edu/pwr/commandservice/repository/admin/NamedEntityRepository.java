package pl.edu.pwr.commandservice.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface NamedEntityRepository<T> extends JpaRepository<T, Long> {
    Optional<T> findByName(String name);
    Set<T> findAllByNameIn(Set<String> names);
}
