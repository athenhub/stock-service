package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Stock 도메인 관련 RabbitMQ 설정.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockConfig {

  private final RabbitStockProperties stockProperties;

  /** stock 관련 이벤트를 발행하는 Topic Exchange. */
  @Bean
  public TopicExchange stockExchange() {
    return new TopicExchange(
        stockProperties.getExchange(), // 예: stock.exchange
        true,
        false);
  }

  /** stock 이벤트를 수신할 Queue. */
  @Bean
  public Queue stockRegisteredQueue() {
    return QueueBuilder.durable(stockProperties.getRegistered().getQueue())
        .build();
  }

  /**
   * Exchange와 Queue를 Binding 한다.
   *
   * <p>routing key를 통해 어떤 메시지를 수신할지 결정된다.
   */
  @Bean
  public Binding stockRegisterdBinding(Queue stockRegisteredQueue, TopicExchange stockExchange) {
    return BindingBuilder.bind(stockRegisteredQueue)
        .to(stockExchange)
        .with(stockProperties.getRegistered().getRoutingKey());
  }
}
