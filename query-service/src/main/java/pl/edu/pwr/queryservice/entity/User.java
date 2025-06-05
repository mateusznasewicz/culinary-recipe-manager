package pl.edu.pwr.queryservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private String role;
}
