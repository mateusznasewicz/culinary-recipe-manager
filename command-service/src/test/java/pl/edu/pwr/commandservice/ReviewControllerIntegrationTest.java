package pl.edu.pwr.commandservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.edu.pwr.commandservice.entity.review.Review;
import pl.edu.pwr.commandservice.repository.ReviewRepository;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        postgreSQLContainer.start();
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void should_createReview() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/review1.json"));

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Review added"));
    }

    @Test
    void shouldFailWithInvalidUserId() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/review1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root).put("userId", 9999L);

        String updatedJson = objectMapper.writeValueAsString(root);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal ID provided in request"));
    }

    @Test
    void shouldFailWithInvalidRecipeId() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/review1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root).put("recipeId", 8888L);

        String updatedJson = objectMapper.writeValueAsString(root);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal ID provided in request"));
    }

    @Test
    void should_updateReviewWhenUsingUserIdAndRecipeId() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/review1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root).put("recipeId", 1L);
        ((ObjectNode) root).put("rating", 3.5);
        String duplicateReviewJson = objectMapper.writeValueAsString(root);

        Long userId = 1L;
        Review existingReview = reviewRepository.findByIdUserIdAndIdRecipeId(userId, 1L).orElseThrow();

        // Porównanie BigDecimal
        assertTrue(existingReview.getRating().compareTo(new BigDecimal("4.5")) == 0);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateReviewJson))
                .andExpect(status().isCreated());

        Review updatedReview = reviewRepository.findByIdUserIdAndIdRecipeId(userId, 1L).orElseThrow();
        assertTrue(updatedReview.getRating().compareTo(new BigDecimal("3.5")) == 0);
    }
}
