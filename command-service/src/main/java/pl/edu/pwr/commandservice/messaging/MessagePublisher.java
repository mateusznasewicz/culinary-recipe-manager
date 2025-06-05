package pl.edu.pwr.commandservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pwr.commandservice.dto.recipe.RecipeEvent;

@Service
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendRecipeMessage(RecipeEvent recipe) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.RECIPE_ROUTING_KEY,
                recipe
        );
    }
}
