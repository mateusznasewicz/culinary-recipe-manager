package pl.edu.pwr.queryservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ingredients_write")
@AllArgsConstructor
@SuperBuilder
public class Ingredient extends NamedEntity {
}
