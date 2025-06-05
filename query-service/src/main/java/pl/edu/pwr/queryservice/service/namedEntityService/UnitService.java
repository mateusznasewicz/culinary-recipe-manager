package pl.edu.pwr.queryservice.service.namedEntityService;

import org.springframework.stereotype.Service;
import pl.edu.pwr.queryservice.entity.Unit;
import pl.edu.pwr.queryservice.repository.namedEntityRepository.UnitRepository;

@Service
public class UnitService extends NamedEntityService<Unit> {
    public UnitService(UnitRepository repository) {
        super(repository);
    }
}
