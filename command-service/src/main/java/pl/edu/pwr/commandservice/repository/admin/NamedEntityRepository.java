package pl.edu.pwr.commandservice.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface NamedEntityRepository<T> extends JpaRepository<T, Long> {
    Optional<T> findByName(String name);
}
