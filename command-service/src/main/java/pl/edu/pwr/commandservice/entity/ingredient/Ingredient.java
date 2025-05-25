package pl.edu.pwr.commandservice.entity.ingredient;

import jakarta.persistence.*;
import pl.edu.pwr.commandservice.entity.NamedEntity;


@Entity
@Table(name = "ingredients_write")
public class Ingredient extends NamedEntity {
}
