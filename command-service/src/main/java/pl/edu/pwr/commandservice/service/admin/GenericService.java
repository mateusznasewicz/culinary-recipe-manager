package pl.edu.pwr.commandservice.service.admin;

public interface GenericService<T, ID> {
    void save(T entity);
    void delete(ID id);
    void update(T entity);
}
