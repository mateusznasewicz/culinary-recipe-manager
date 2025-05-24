package pl.edu.pwr.commandservice.service.admin;


import jakarta.persistence.EntityNotFoundException;
import pl.edu.pwr.commandservice.entity.NamedEntity;
import pl.edu.pwr.commandservice.repository.admin.NamedEntityRepository;

public class AdminService<T extends NamedEntity> implements GenericService<T, Long> {

    private final NamedEntityRepository<T> repository;
    private final Class<T> clazz;

    public AdminService(NamedEntityRepository<T> repository, Class<T> clazz) {
        this.repository = repository;
        this.clazz = clazz;
    }

    @Override
    public void save(String name) {
        repository.findByName(name).ifPresent(existing -> {
            throw new IllegalArgumentException("Entity with name '" + name + "' already exists.");
        });

        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            entity.setName(name);
            repository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create entity via reflection", e);
        }
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
