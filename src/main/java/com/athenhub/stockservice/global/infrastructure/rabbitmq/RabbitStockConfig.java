package com.athenhub.stockservice.global.infrastructure.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Stock 도메인 관련 RabbitMQ 설정
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
  public Queue stockQueue() {
    return QueueBuilder.durable(stockProperties.getQueue()) // 예: stock.queue
        .build();
  }

  /**
   * Exchange와 Queue를 Binding 한다.
   *
   * <p>routing key를 통해 어떤 메시지를 수신할지 결정된다.
   */
  @Bean
  public Binding stockBinding(Queue stockQueue, TopicExchange stockExchange) {
    return BindingBuilder.bind(stockQueue)
        .to(stockExchange)
        .with(stockProperties.getRoutingKey()); // 예: stock.*
  }

  /** JSON 기반 메시지 직렬화 Converter. */
  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
