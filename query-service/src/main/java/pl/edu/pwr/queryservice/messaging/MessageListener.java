package pl.edu.pwr.queryservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.edu.pwr.queryservice.dto.RecipeDTO;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(Message message) throws IOException {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if(routingKey.equals(RabbitMQConfig.RECIPE_ROUTING_KEY)) {
            logger.info("Received recipe from message");
            RecipeDTO recipeDTO = objectMapper.readValue(message.getBody(), RecipeDTO.class);
        }
    }
}
