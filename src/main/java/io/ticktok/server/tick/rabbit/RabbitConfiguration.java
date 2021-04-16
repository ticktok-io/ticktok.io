package io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;

@Configuration
@Profile("rabbit")
public class RabbitConfiguration {

    private static final String EXCHANGE_NAME = "ticktok.tick.exchange";

    @Autowired
    private RabbitProperties rabbitProperties;

    @Bean
    public TopicExchange ticktokExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUri(URI.create(rabbitProperties.getUri()));
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin admin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public TickChannelOperations tickChannelExplorer(AmqpAdmin amqpAdmin, TopicExchange topicExchange) {
        return new RabbitTickChannelOperations(rabbitProperties, amqpAdmin, topicExchange);
    }

    @Bean
    public TickPublisher tickPublisher(RabbitTemplate rabbitTemplate, Exchange ticktokExchange) {
        return new RabbitTickPublisher(rabbitTemplate, ticktokExchange);
    }

}