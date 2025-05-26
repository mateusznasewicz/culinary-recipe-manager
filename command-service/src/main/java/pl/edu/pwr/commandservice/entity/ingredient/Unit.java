package pl.edu.pwr.commandservice.entity.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.edu.pwr.commandservice.entity.NamedEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "units_write")
@AllArgsConstructor
@SuperBuilder
public class Unit extends NamedEntity {
}
