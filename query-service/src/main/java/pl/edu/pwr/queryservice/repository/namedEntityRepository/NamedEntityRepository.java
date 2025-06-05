package pl.edu.pwr.queryservice.repository.namedEntityRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface NamedEntityRepository<T> extends JpaRepository<T, Long> {
    Set<T> findByQuery(String q, int limit);
    Optional<String> findNameById(Long id);
}
