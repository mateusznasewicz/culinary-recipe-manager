package pl.edu.pwr.queryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Set;

@NoRepositoryBean
public interface NamedEntityRepository<T> extends JpaRepository<T, Long> {
    Set<T> findByQuery(String q, int limit);
}
