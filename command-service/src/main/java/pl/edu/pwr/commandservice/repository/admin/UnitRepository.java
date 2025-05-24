package pl.edu.pwr.commandservice.repository.admin;

import org.springframework.stereotype.Repository;
import pl.edu.pwr.commandservice.entity.ingredient.Unit;

@Repository
public interface UnitRepository extends NamedEntityRepository<Unit> {
}
