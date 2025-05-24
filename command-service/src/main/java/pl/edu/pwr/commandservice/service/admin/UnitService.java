package pl.edu.pwr.commandservice.service.admin;

import org.springframework.stereotype.Service;
import pl.edu.pwr.commandservice.entity.ingredient.Unit;
import pl.edu.pwr.commandservice.repository.admin.UnitRepository;

@Service
public class UnitService extends AdminService<Unit> {
    public UnitService(UnitRepository repository) {
        super(repository, Unit.class);
    }
}
