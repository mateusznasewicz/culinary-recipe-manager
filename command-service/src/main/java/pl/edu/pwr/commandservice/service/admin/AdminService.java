package pl.edu.pwr.commandservice.service.admin;


import jakarta.persistence.EntityNotFoundException;
import pl.edu.pwr.commandservice.entity.NamedEntity;
import pl.edu.pwr.commandservice.repository.admin.NamedEntityRepository;

public class AdminService<T extends NamedEntity> implements GenericService<T, Long> {

    private final NamedEntityRepository<T> repository;

    public AdminService(NamedEntityRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public void save(T entity) {
        repository.findByName(entity.getName()).ifPresent(existing -> {
            throw new IllegalArgumentException("Entity with name '" + entity.getName() + "' already exists.");
        });

        repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Entity does not exist.");
        }

        repository.deleteById(id);
    }

    @Override
    public void update(T entity) {
        if (entity.getId() == null || !repository.existsById(entity.getId())) {
            throw new EntityNotFoundException("Entity does not exist.");
        }

        repository.save(entity);
    }
}
