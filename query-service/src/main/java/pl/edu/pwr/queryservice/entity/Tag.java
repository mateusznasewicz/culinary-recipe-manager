package pl.edu.pwr.queryservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tags_write")
@AllArgsConstructor
@SuperBuilder
public class Tag extends NamedEntity{
}
