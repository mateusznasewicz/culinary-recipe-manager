package pl.edu.pwr.commandservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pwr.commandservice.dto.RecipeDTO;
import pl.edu.pwr.commandservice.entity.review.Review;

@Service
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendRecipeMessage(RecipeDTO recipe) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.RECIPE_ROUTING_KEY,
                recipe
        );
    }

    public void sendReviewMessage(Review review) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.REVIEW_ROUTING_KEY,
                review
        );
    }
}
