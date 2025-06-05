package pl.edu.pwr.queryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pwr.queryservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.username FROM User u WHERE u.id = :id")
    Optional<String> findUsernameById(@Param("id") Long id);
}
