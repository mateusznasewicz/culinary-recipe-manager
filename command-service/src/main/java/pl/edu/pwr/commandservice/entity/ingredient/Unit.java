package pl.edu.pwr.commandservice.entity.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pwr.commandservice.entity.NamedEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "units_write")
@Data
public class Unit extends NamedEntity {
}
