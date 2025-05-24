package pl.edu.pwr.commandservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class NamedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, unique = true)
    protected String name;
}
