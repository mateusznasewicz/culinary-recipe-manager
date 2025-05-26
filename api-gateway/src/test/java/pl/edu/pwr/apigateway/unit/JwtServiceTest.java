package pl.edu.pwr.apigateway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.edu.pwr.apigateway.auth.AuthRepository;
import pl.edu.pwr.apigateway.entity.User;
import pl.edu.pwr.apigateway.jwt.JwtService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private AuthRepository authRepository;

    private final String secretKey = "dGhpc2lzYXNlY3JldGtleXdoaWNoaXNsb25nZXJ0aGFuMzJiZXl0ZXM=";
    private final long jwtExpiration = 1000 * 60 * 60;

    private User createUser(){
        String username = "alice";
        User user = new User();
        user.setUsername(username);
        user.setRole("ADMIN");

        return user;
    }

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
    }

    @Test
    void should_generateValidToken_when_userExists() {
        // Given
        User user = createUser();
        String username = user.getUsername();

        when(authRepository.findByUsername(username)).thenReturn(Mono.just(user));

        // When & Then
        StepVerifier.create(jwtService.generateToken(username))
                .assertNext(token -> {
                    assertNotNull(token);
                    assertTrue(jwtService.isTokenValid(token));
                    assertEquals("alice", jwtService.getUsername(token));
                    assertEquals("ADMIN", jwtService.getRole(token));
                })
                .verifyComplete();
    }

    @Test
    void should_returnFalse_when_tokenIsInvalid() {
        // Given
        String invalidToken = "this.is.not.a.jwt";

        // When
        boolean isValid = jwtService.isTokenValid(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void should_returnFalse_when_tokenIsExpired() throws Exception {
        // Given
        Field expirationField = JwtService.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 1L);
        User user = createUser();
        String username = user.getUsername();

        when(authRepository.findByUsername(username)).thenReturn(Mono.just(user));
        String token = jwtService.generateToken(username).block();

        Thread.sleep(10);

        // When
        boolean isValid = jwtService.isTokenValid(token);

        // Then
        assertFalse(isValid);
    }
}
