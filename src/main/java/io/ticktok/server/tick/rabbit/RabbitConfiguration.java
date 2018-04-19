package io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.TickChannelFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class RabbitConfiguration {

    private static final String EXCHANGE_NAME = "ticktok.tick.exchange";

    @Value("${rabbit.uri}")
    private String rabbitUri;

    /*@Bean
    public TopicExchange tickExchange() {
        return new TopicExchange(EXCHANGE_NAME, false, false);
    }*/

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(URI.create(rabbitUri));
    }

    /*@Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }*/

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
    
    @Bean
    public TickChannelFactory tickChannelFactory() {
        return new RabbitTickChannelFactory(rabbitUri, EXCHANGE_NAME);
    }

}
