package pl.edu.pwr.queryservice.repository.namedEntityRepository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class NamedEntityRepositoryImpl<T>
        extends SimpleJpaRepository<T, Long>
        implements NamedEntityRepository<T> {

    private final EntityManager entityManager;
    private final String tableName;
    private final Class<T> clazz;

    public NamedEntityRepositoryImpl(EntityManager entityManager, String tableName, Class<T> clazz) {
        super(clazz, entityManager);
        this.entityManager = entityManager;
        this.tableName = tableName;
        this.clazz = clazz;
    }

    @Override
    public Optional<String> findNameById(Long id){
        String sql = String.format("SELECT name FROM %s WHERE id = :id", tableName);
        String result = entityManager.createNativeQuery(sql)
                .setParameter("id", id)
                .getSingleResult().toString();
        return Optional.of(result);
    }

    @Override
    public Set<T> findByQuery(String q, int limit){
        String sql = String.format("""
                SELECT id,name FROM
                (SELECT *, similarity(name, :q) AS sim FROM %s)
                WHERE sim > :sim_treshold
                ORDER BY sim DESC
                LIMIT :limit
                """, tableName);
        double sim_treshold = 0.2;


        @SuppressWarnings("unchecked")
        List<T> result = entityManager.createNativeQuery(sql, clazz)
                .setParameter("q", q)
                .setParameter("sim_treshold", sim_treshold)
                .setParameter("limit", limit)
                .getResultList();

        return new HashSet<>(result);
    }
}
