package pl.edu.pwr.commandservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.pwr.commandservice.dto.recipe.RecipeAMQP;
import pl.edu.pwr.commandservice.dto.recipe.RecipeAMQP;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "culinary.events.queue";
    public static final String EXCHANGE_NAME = "culinary.events.exchange";
    public static final String RECIPE_ROUTING_KEY = "recipe.created";
    public static final String REVIEW_ROUTING_KEY = "review.created";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding recipeBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RECIPE_ROUTING_KEY);
    }

    @Bean
    public Binding reviewBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(REVIEW_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = Map.of("RecipeDTO", RecipeAMQP.class);
        typeMapper.setIdClassMapping(idClassMapping);
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
