package pl.edu.pwr.apigateway.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pwr.apigateway.entity.User;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialException;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public Mono<Boolean> userExists(String username){
        return authRepository.findByUsername(username)
                .map(user -> true)
                .defaultIfEmpty(false);
    }

    public Mono<Void> login(String username, String password) {
        return authRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new CredentialException("Username or password is incorrect")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new CredentialException("Username or password is incorrect"));
                    }
                    return Mono.empty();
                }).then();
    }

    public Mono<Void> register(String username, String password){
        return userExists(username)
                .flatMap(exists -> {
                    if (exists) return Mono.error(new CredentialException("User already exists"));
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setRole("USER");

                    return authRepository.save(user).then();
                });
    }


}
