package pl.edu.pwr.commandservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tags_write")
@Data
public class Tag extends NamedEntity{

}
