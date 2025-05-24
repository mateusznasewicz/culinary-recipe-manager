package pl.edu.pwr.commandservice.entity.ingredient;

import jakarta.persistence.*;
import lombok.Data;
import pl.edu.pwr.commandservice.entity.NamedEntity;


@Entity
@Table(name = "ingredients_write")
@Data
public class Ingredient extends NamedEntity {
}
