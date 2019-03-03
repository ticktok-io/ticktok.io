package io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.TickChannelExplorer;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        return new CachingConnectionFactory(URI.create(rabbitProperties.getUri()));
    }

    @Bean
    public AmqpAdmin admin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public TickChannelExplorer tickChannelExplorer(AmqpAdmin amqpAdmin, TopicExchange topicExchange) {
        return new RabbitTickChannelExplorer(rabbitProperties, amqpAdmin, topicExchange);
    }

    @Bean
    public TickPublisher tickPublisher(RabbitTemplate rabbitTemplate, Exchange ticktokExchange) {
        return new RabbitTickPublisher(rabbitTemplate, ticktokExchange);
    }

}
