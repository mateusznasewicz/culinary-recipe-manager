package pl.edu.pwr.commandservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
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

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void should_AddRecipeSuccessfully() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/recipe1.json"));

        mockMvc.perform(post("/api/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                ).andExpect(status().isCreated())
                .andExpect(content().string("Recipe added"));
    }

    @Test
    @Transactional
    void should_failAddRecipe_whenBadIngredientID() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/recipe1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root.get("ingredientUnits").get(0)).put("ingredientId", 100L);

        String updatedJson = objectMapper.writeValueAsString(root);

        mockMvc.perform(post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson)
                ).andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal ID provided in request"));
    }

    @Test
    @Transactional
    void should_failAddRecipe_whenBadTag() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/recipe1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root.get("tags").get(0)).put("name", "Polska");

        String updatedJson = objectMapper.writeValueAsString(root);

        mockMvc.perform(post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson)
                ).andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal ID provided in request"));
    }

    @Test
    @Transactional
    void should_failAddRecipe_whenBadUnitID() throws Exception {
        String json = Files.readString(Path.of("src/test/resources/recipe1.json"));

        JsonNode root = objectMapper.readTree(json);
        ((ObjectNode) root.get("ingredientUnits").get(0)).put("unitId", 100L);

        String updatedJson = objectMapper.writeValueAsString(root);

        mockMvc.perform(post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson)
                ).andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal ID provided in request"));
    }
}
