package pl.edu.pwr.queryservice.service.namedEntityService;

import pl.edu.pwr.queryservice.repository.namedEntityRepository.NamedEntityRepository;

import java.util.Set;

public class NamedEntityService<T> {
    private NamedEntityRepository<T> repository;

    public NamedEntityService(NamedEntityRepository<T> repository) {
        this.repository = repository;
    }

    public Set<T> findByQuery(String query){
        int limit = 10;
        return repository.findByQuery(query, limit);
    }
}