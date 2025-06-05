package pl.edu.pwr.queryservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.edu.pwr.queryservice.dto.RecipeAMQP;
import pl.edu.pwr.queryservice.dto.RecipeMapper;
import pl.edu.pwr.queryservice.entity.Recipe;
import pl.edu.pwr.queryservice.service.RecipeService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final RecipeMapper recipeMapper;
    private final RecipeService recipeService;
    private Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(Message message) throws IOException {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if(routingKey.equals(RabbitMQConfig.RECIPE_ROUTING_KEY)) {
            logger.info("Received recipe from message");
            RecipeAMQP recipe = objectMapper.readValue(message.getBody(), RecipeAMQP.class);
            Recipe entity  = recipeMapper.toEntity(recipe);
            logger.info(entity.toString());
            recipeService.save(entity);
        }
    }
}
