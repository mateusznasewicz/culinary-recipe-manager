package pl.edu.pwr.queryservice.repository.namedEntityRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pl.edu.pwr.queryservice.entity.Unit;

@Repository
public class UnitRepository extends NamedEntityRepositoryImpl<Unit>{
    public UnitRepository(EntityManager em) {
        super(em,"units_write", Unit.class);
    }
}
