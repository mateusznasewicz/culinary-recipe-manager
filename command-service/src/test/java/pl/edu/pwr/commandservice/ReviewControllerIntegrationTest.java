package pl.edu.pwr.commandservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Transactional
    void should_createReview() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/review1.json"));

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1L)
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
                        .header("X-User-Id", 1L)
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
                        .header("X-User-Id", 1L)
                        .content(updatedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal ID provided in request"));
    }

    @Test
    @Transactional
    void should_updateReview_whenUsingUserIdAndRecipeId() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/review1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root).put("recipeId", 1L);
        ((ObjectNode) root).put("rating", 3.5);
        String duplicateReviewJson = objectMapper.writeValueAsString(root);

        Long userId = 1L;
        Review existingReview = reviewRepository.findByIdUserIdAndIdRecipeId(userId, 1L).orElseThrow();
        System.out.println(existingReview.getRating().toString());
        assertEquals(0, existingReview.getRating().compareTo(new BigDecimal("4.5")));

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1L)
                        .content(duplicateReviewJson))
                .andExpect(status().isCreated());

        Review updatedReview = reviewRepository.findByIdUserIdAndIdRecipeId(userId, 1L).orElseThrow();
        assertEquals(0, updatedReview.getRating().compareTo(new BigDecimal("3.5")));
    }

    @Test
    @Transactional
    void should_notUpdateReview_whenUsingDifferentUserId() throws Exception {
        // given
        String json = Files.readString(Path.of("src/test/resources/review1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root).put("recipeId", 1L);
        ((ObjectNode) root).put("rating", 3.5);
        String duplicateReviewJson = objectMapper.writeValueAsString(root);

        Long userId = 1L;
        Long differentUserId = 2L;
        Review existingReview = reviewRepository.findByIdUserIdAndIdRecipeId(userId, 1L).orElseThrow();

        assertEquals(0, existingReview.getRating().compareTo(new BigDecimal("4.5")));

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", differentUserId)
                        .content(duplicateReviewJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal ID provided in request"));

        Review unchangedReview = reviewRepository.findByIdUserIdAndIdRecipeId(userId, 1L).orElseThrow();
        assertEquals(0, unchangedReview.getRating().compareTo(new BigDecimal("4.5")));
    }
}
