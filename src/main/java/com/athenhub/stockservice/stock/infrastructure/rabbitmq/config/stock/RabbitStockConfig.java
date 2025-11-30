package com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 설정 클래스.
 *
 * <p>재고 감소 이벤트의 Main / Retry / DLQ 라우팅을 구성한다.
 *
 * <p>DLQ는 별도 Exchange로 분리하여 장애 메시지를 격리한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockConfig {

  private final RabbitStockProperties props;

  /** 정상 이벤트를 처리하는 메인 Exchange. */
  @Bean
  public TopicExchange stockExchange() {
    return new TopicExchange(props.getExchange(), true, false);
  }

  /** 최종 실패 메시지를 수집하는 DLQ 전용 Exchange. */
  @Bean
  public TopicExchange stockDlqExchange() {
    return new TopicExchange(props.getDlqExchange(), true, false);
  }

  /**
   * 재고 감소 메인 Queue.
   *
   * <p>처리 실패 시 Retry Queue로 이동하도록 DLX를 설정한다.
   */
  @Bean
  public Queue stockDecreaseQueue() {
    return QueueBuilder.durable(props.getDecrease().getQueue())
        .withArgument("x-dead-letter-exchange", props.getExchange())
        .withArgument("x-dead-letter-routing-key", props.getDecreaseRetry().getRoutingKey())
        .build();
  }

  /** 메인 Queue 바인딩. */
  @Bean
  public Binding stockDecreaseBinding() {
    return BindingBuilder.bind(stockDecreaseQueue())
        .to(stockExchange())
        .with(props.getDecrease().getRoutingKey());
  }

  /**
   * 재시도 처리를 위한 Retry Queue.
   *
   * <p>TTL 후 메인 Queue로 재유입된다.
   */
  @Bean
  public Queue stockDecreaseRetryQueue() {
    return QueueBuilder.durable(props.getDecreaseRetry().getQueue())
        .withArgument("x-message-ttl", props.getDecreaseRetry().getTtl())
        .withArgument("x-dead-letter-exchange", props.getExchange())
        .withArgument("x-dead-letter-routing-key", props.getDecrease().getRoutingKey())
        .build();
  }

  /** Retry Queue 바인딩. */
  @Bean
  public Binding stockDecreaseRetryBinding() {
    return BindingBuilder.bind(stockDecreaseRetryQueue())
        .to(stockExchange())
        .with(props.getDecreaseRetry().getRoutingKey());
  }

  /** 최종 실패 메시지를 격리하는 Dead Letter Queue. */
  @Bean
  public Queue stockDecreaseDeadLetterQueue() {
    return QueueBuilder.durable(props.getDecreaseDead().getQueue()).build();
  }

  /** Dead Letter Queue 바인딩. */
  @Bean
  public Binding stockDecreaseDeadLetterBinding() {
    return BindingBuilder.bind(stockDecreaseDeadLetterQueue())
        .to(stockDlqExchange())
        .with(props.getDecreaseDead().getRoutingKey());
  }
}
