package pl.edu.pwr.commandservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tags_write")
@AllArgsConstructor
@SuperBuilder
public class Tag extends NamedEntity{
}
