package pl.edu.pwr.apigateway.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pwr.apigateway.auth.AuthRepository;
import pl.edu.pwr.apigateway.auth.AuthService;
import pl.edu.pwr.apigateway.entity.User;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.security.auth.login.CredentialException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void should_returnTrue_whenUserExists() {
        // given
        String username = "john";
        User user = new User();
        user.setUsername(username);
        when(authRepository.findByUsername(username)).thenReturn(Mono.just(user));

        // when
        Mono<Boolean> result = authService.userExists(username);

        // then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void should_returnFalse_whenUserDoesNotExist() {
        // given
        String username = "unknown";
        when(authRepository.findByUsername(username)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = authService.userExists(username);

        // then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void should_completeLogin_whenCredentialsAreCorrect() {
        // given
        String username = "john";
        String rawPassword = "pass";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPass");

        when(authRepository.findByUsername(username)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);

        // when
        Mono<Void> result = authService.login(username, rawPassword);

        // then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void should_errorLogin_whenUserNotFound() {
        // given
        String username = "unknown";
        String password = "pass";

        when(authRepository.findByUsername(username)).thenReturn(Mono.empty());

        // when
        Mono<Void> result = authService.login(username, password);

        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CredentialException &&
                        throwable.getMessage().equals("Username or password is incorrect"))
                .verify();
    }

    @Test
    void should_errorLogin_whenPasswordDoesNotMatch() {
        // given
        String username = "john";
        String rawPassword = "wrongPass";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPass");

        when(authRepository.findByUsername(username)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

        // when
        Mono<Void> result = authService.login(username, rawPassword);

        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CredentialException &&
                        throwable.getMessage().equals("Username or password is incorrect"))
                .verify();
    }

    @Test
    void should_completeRegister_whenUserDoesNotExist() {
        // given
        String username = "newUser";
        String rawPassword = "pass";
        User savedUser = new User();
        savedUser.setUsername(username);
        savedUser.setPassword("encodedPass");
        savedUser.setRole("USER");

        when(authRepository.findByUsername(username)).thenReturn(Mono.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPass");
        when(authRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // when
        Mono<Void> result = authService.register(username, rawPassword, "USER");

        // then
        StepVerifier.create(result)
                .verifyComplete();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(authRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(username, capturedUser.getUsername());
        assertEquals("encodedPass", capturedUser.getPassword());
        assertEquals("ROLE_USER", capturedUser.getRole());
    }

    @Test
    void should_errorRegister_whenUserExists() {
        // given
        String username = "existingUser";
        String rawPassword = "pass";
        User existingUser = new User();
        existingUser.setUsername(username);

        when(authRepository.findByUsername(username)).thenReturn(Mono.just(existingUser));

        // when
        Mono<Void> result = authService.register(username, rawPassword, "USER");

        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CredentialException &&
                        throwable.getMessage().equals("User already exists"))
                .verify();

        verify(authRepository, never()).save(any());
    }
}
