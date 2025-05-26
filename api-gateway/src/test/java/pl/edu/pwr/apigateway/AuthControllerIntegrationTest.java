package pl.edu.pwr.apigateway;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.edu.pwr.apigateway.auth.AuthRepository;
import pl.edu.pwr.apigateway.config.RouteConfiguration;
import pl.edu.pwr.apigateway.config.SecurityConfig;
import pl.edu.pwr.apigateway.dto.LoginRequest;
import pl.edu.pwr.apigateway.dto.RegisterRequest;
import pl.edu.pwr.apigateway.entity.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    DatabaseClient databaseClient;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        postgresContainer.start();
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        postgresContainer.getHost(),
                        postgresContainer.getFirstMappedPort(),
                        postgresContainer.getDatabaseName()
                )
        );
        registry.add("spring.r2dbc.username", postgresContainer::getUsername);
        registry.add("spring.r2dbc.password", postgresContainer::getPassword);
    }

    @BeforeAll
    void setup() throws IOException {
        String schemaSql = Files.readString(Paths.get("src/main/resources/schema.sql"));
        databaseClient.sql(schemaSql)
                .then()
                .block();
    }

    @AfterEach
    void cleanAfterEach() {
        authRepository.deleteAll().block();
    }

    @Test
    void should_registerUserSuccessfully() {
        RegisterRequest request = new RegisterRequest("testuser", "testpass", "testpass");

        webTestClient.post()
                .uri("/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Registration successful");

        boolean exists = authRepository.findByUsername("testuser").blockOptional().isPresent();
        assertTrue(exists);
    }

    @Test
    void should_not_registerUserWhenUsernameExists() {
        authRepository.save(new User(null, "existingUser", "hashedPassword", "USER")).block();
        RegisterRequest request = new RegisterRequest("existingUser", "testpass", "testpass");

        webTestClient.post()
                .uri("/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(String.class)
                .isEqualTo("User already exists");
    }

    @Test
    void should_loginSuccessfully() {
        User user = new User(null, "loginUser", passwordEncoder.encode("securePass"), "USER");
        authRepository.save(user).block();

        LoginRequest loginRequest = new LoginRequest("loginUser", "securePass");

        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isNotEmpty()
                .jsonPath("$.type").isEqualTo("Bearer")
                .jsonPath("$.message").isEqualTo("Login successful");
    }

    @Test
    void should_failLoginWithWrongPassword() {
        User user = new User(null, "loginUser2", passwordEncoder.encode("pass"), "USER");
        authRepository.save(user).block();

        LoginRequest loginRequest = new LoginRequest("loginUser2", "wrongPass");

        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(String.class)
                .isEqualTo("Username or password is incorrect");
    }

    @Test
    void should_failRegistrationWhenCredentialsEmpty() {
        RegisterRequest request = new RegisterRequest("", "", "");

        webTestClient.post()
                .uri("/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.username").isEqualTo("Username must not be blank")
                .jsonPath("$.password").isEqualTo("Password must not be blank")
                .jsonPath("$.confirmPassword").isEqualTo("Confirm password must not be blank");
    }

    @Test
    void should_failRegistrationWhenPasswordsNotMatch() {
        RegisterRequest request = new RegisterRequest("testuser", "testpass", "wrongPass");
        webTestClient.post()
                .uri("/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("formError").isEqualTo("Passwords do not match");
    }

    @Test
    void should_failLoginWhenCredentialsEmpty() {
        LoginRequest loginRequest = new LoginRequest("", "");

        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.username").isEqualTo("Username must not be blank")
                .jsonPath("$.password").isEqualTo("Password must not be blank");
    }
}
