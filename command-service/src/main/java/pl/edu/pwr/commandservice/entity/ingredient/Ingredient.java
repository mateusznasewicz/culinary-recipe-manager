package pl.edu.pwr.commandservice.entity.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.edu.pwr.commandservice.entity.NamedEntity;


@Entity
@Table(name = "ingredients_write")
@AllArgsConstructor
@SuperBuilder
public class Ingredient extends NamedEntity {
}
