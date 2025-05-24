package pl.edu.pwr.apigateway.auth;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pwr.apigateway.entity.User;
import reactor.core.publisher.Mono;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);
}
