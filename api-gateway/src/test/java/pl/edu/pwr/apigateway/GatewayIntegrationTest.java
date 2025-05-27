package pl.edu.pwr.apigateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import pl.edu.pwr.apigateway.auth.AuthService;
import pl.edu.pwr.apigateway.jwt.JwtService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GatewayIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    DatabaseClient databaseClient;

    private WireMockServer readServiceMock;
    private WireMockServer writeServiceMock;

    private String adminToken;
    private String userToken;

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

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
    void registerUser() throws IOException {
        String schemaSql = Files.readString(Paths.get("src/main/resources/schema.sql"));
        databaseClient.sql(schemaSql)
                .then()
                .block();
        authService.register("admin", "password", "ADMIN").block();
        authService.register("user", "password", "USER").block();
        adminToken = jwtService.generateToken("admin").block();
        userToken = jwtService.generateToken("user").block();
    }

    @BeforeEach
    void setup(){
        readServiceMock = new WireMockServer(8081);
        writeServiceMock = new WireMockServer(8082);
        readServiceMock.start();
        writeServiceMock.start();

        readServiceMock.stubFor(get(urlPathMatching("/api/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Read service response\"}")));

        writeServiceMock.stubFor(post(urlPathMatching("/api/.*"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Write service created\"}")));

        writeServiceMock.stubFor(put(urlPathMatching("/api/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Write service updated\"}")));

        writeServiceMock.stubFor(delete(urlPathMatching("/api/.*"))
                .willReturn(aResponse()
                        .withStatus(204)));
    }

    @AfterEach
    void tearDown() {
        readServiceMock.stop();
        writeServiceMock.stop();
    }

    @Test
    void should_routeGetRequest_toReadService() {
        webTestClient.get()
                .uri("/api/recipes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("{\"message\": \"Read service response\"}");
    }

    @Test
    void should_UnauthorizedGetRequest_toReadService() {
        webTestClient.get()
                .uri("/api/recipes")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void should_routePostRequest_toWriteService() {
        webTestClient.post()
                .uri("/api/recipes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\": \"Spaghetti Carbonara\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("{\"message\": \"Write service created\"}");
    }

    @Test
    void should_UnauthorizedPostRequest_toWriteService() {
        webTestClient.post()
                .uri("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\": \"Spaghetti Carbonara\"}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void should_routePutRequest_toWriteService() {
        webTestClient.put()
                .uri("/api/recipes/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\": \"Updated Carbonara\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("{\"message\": \"Write service updated\"}");
    }

    @Test
    void should_UnauthorizedPutRequest_toWriteService() {
        webTestClient.put()
                .uri("/api/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\": \"Updated Carbonara\"}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void should_routeDeleteRequest_toWriteService() {
        webTestClient.delete()
                .uri("/api/recipes/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void should_UnauthorizedDeleteRequest_toWriteService() {
        webTestClient.delete()
                .uri("/api/recipes/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void should_allowAdminToPostTag() {
        webTestClient.post()
                .uri("/api/tag")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Vegan\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("{\"message\": \"Write service created\"}");
    }

    @Test
    void should_forbidUserToPostTag() {
        webTestClient.post()
                .uri("/api/tag")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Vegan\"}")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void should_allowAdminToPostUnit() {
        webTestClient.post()
                .uri("/api/unit")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"g\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("{\"message\": \"Write service created\"}");
    }

    @Test
    void should_forbidUserToPostUnit() {
        webTestClient.post()
                .uri("/api/unit")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"g\"}")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void should_allowAdminToPostIngredient() {
        webTestClient.post()
                .uri("/api/ingredient")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Egg\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("{\"message\": \"Write service created\"}");
    }

    @Test
    void should_forbidUserToPostIngredient() {
        webTestClient.post()
                .uri("/api/ingredient")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Egg\"}")
                .exchange()
                .expectStatus().isForbidden();
    }

}
