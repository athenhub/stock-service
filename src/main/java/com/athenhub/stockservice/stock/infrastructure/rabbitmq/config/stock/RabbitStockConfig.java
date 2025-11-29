package com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock;

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
 * Stock 도메인 관련 RabbitMQ 설정 클래스이다.
 *
 * <p>재고 서비스에서 발행하는 이벤트를 처리하기 위해 Exchange, Queue, Binding 구성을 정의한다.
 *
 * <p>구성 요소:
 *
 * <ul>
 *   <li>Stock 이벤트용 Topic Exchange
 *   <li>재고 등록 이벤트 Queue 및 Binding
 *   <li>재고 감소 이벤트 Main Queue / Retry Queue / Dead Letter Queue 및 Binding
 * </ul>
 *
 * <p>메시지 흐름:
 *
 * <pre>
 *   [stock.decreased.queue] (Main)
 *           ↓ 실패 시 DLX
 *   [stock.decreased.retry.queue] (TTL 후 재시도)
 *           ↓ TTL 만료 시 DLX
 *   [stock.decreased.queue] (Main 재유입)
 *
 *   (최종 포기 시, 리스너에서 DeadLetter RoutingKey로 직접 발행)
 *           ↓
 *   [stock.decrease.dead.queue] (DLQ)
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockConfig {

  private final RabbitStockProperties stockProperties;

  /**
   * Stock 관련 이벤트를 발행하는 Topic Exchange이다.
   *
   * <p>예: {@code stock.exchange}
   *
   * @return Stock 이벤트용 TopicExchange
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public TopicExchange stockExchange() {
    return new TopicExchange(
        stockProperties.getExchange(), // 예: stock.exchange
        true, // durable
        false // autoDelete
        );
  }

  /**
   * 재고 등록 이벤트를 처리하기 위한 Queue이다.
   *
   * <p>예: {@code stock.registered.queue}
   *
   * @return 재고 등록 이벤트용 Queue Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Queue stockRegisteredQueue() {
    return QueueBuilder.durable(stockProperties.getRegistered().getQueue()).build();
  }

  /**
   * 재고 등록 이벤트용 Binding이다.
   *
   * <p>Exchange로 발행된 재고 등록 이벤트를 등록 Queue로 라우팅한다.
   *
   * @param stockRegisteredQueue 재고 등록 이벤트 Queue
   * @param stockExchange Stock Topic Exchange
   * @return 재고 등록 이벤트 Binding Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockRegisteredBinding(Queue stockRegisteredQueue, TopicExchange stockExchange) {

    return BindingBuilder.bind(stockRegisteredQueue)
        .to(stockExchange)
        .with(stockProperties.getRegistered().getRoutingKey());
  }

  /**
   * 재고 감소 이벤트를 처리하기 위한 Main Queue이다.
   *
   * <p>예: {@code stock.decreased.queue}
   *
   * <p>리스너에서 처리 실패 시, Dead-Letter 설정을 통해 Retry Queue로 메시지를 이동시킨다.
   *
   * @return 재고 감소 이벤트용 Main Queue Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Queue stockDecreaseQueue() {
    return QueueBuilder.durable(stockProperties.getDecrease().getQueue())
        // 실패한 메시지를 Retry Queue로 전달하기 위한 DLX 설정
        .withArgument("x-dead-letter-exchange", stockProperties.getExchange())
        .withArgument(
            "x-dead-letter-routing-key",
            stockProperties.getDecreaseRetry().getRoutingKey()) // 실패 → Retry Queue로
        .build();
  }

  /**
   * 재고 감소 이벤트용 Main Queue Binding이다.
   *
   * <p>Exchange로 발행된 재고 감소 이벤트를 Main Queue로 라우팅한다.
   *
   * @param stockDecreaseQueue 재고 감소 이벤트 Main Queue
   * @param stockExchange Stock Topic Exchange
   * @return 재고 감소 이벤트 Main Queue Binding Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockDecreaseBinding(Queue stockDecreaseQueue, TopicExchange stockExchange) {

    return BindingBuilder.bind(stockDecreaseQueue)
        .to(stockExchange)
        .with(stockProperties.getDecrease().getRoutingKey());
  }

  /**
   * 재고 감소 이벤트 재시도를 위한 Retry Queue이다.
   *
   * <p>예: {@code stock.decreased.retry.queue}
   *
   * <p>TTL이 지난 후 Dead-Letter 설정을 통해 다시 Main Queue로 메시지를 재전달한다.
   *
   * @return 재고 감소 이벤트 Retry Queue Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Queue stockDecreaseRetryQueue() {
    return QueueBuilder.durable(stockProperties.getDecreaseRetry().getQueue())
        // Retry 지연 처리용 TTL (ms 단위)
        .withArgument("x-message-ttl", stockProperties.getDecreaseRetry().getTtl())
        // TTL 만료 후 다시 Main Queue로 보내기 위한 DLX 설정
        .withArgument("x-dead-letter-exchange", stockProperties.getExchange())
        .withArgument(
            "x-dead-letter-routing-key",
            stockProperties.getDecrease().getRoutingKey()) // TTL 만료 → Main Queue로 재유입
        .build();
  }

  /**
   * 재고 감소 이벤트 Retry Queue Binding이다.
   *
   * <p>Exchange로 발행된 Retry용 RoutingKey를 Retry Queue에 바인딩한다.
   *
   * @param stockDecreaseRetryQueue 재고 감소 이벤트 Retry Queue
   * @param stockExchange Stock Topic Exchange
   * @return 재고 감소 이벤트 Retry Queue Binding Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockDecreaseRetryBinding(
      Queue stockDecreaseRetryQueue, TopicExchange stockExchange) {

    return BindingBuilder.bind(stockDecreaseRetryQueue)
        .to(stockExchange)
        .with(stockProperties.getDecreaseRetry().getRoutingKey());
  }

  /**
   * 재고 감소 이벤트 처리 실패 시 최종적으로 격리할 Dead Letter Queue이다.
   *
   * <p>예: {@code stock.decrease.dead.queue}
   *
   * <p>Retry를 모두 소진하거나, 리스너가 직접 포기한 메시지를 수집하는 용도로 사용한다.
   *
   * @return 재고 감소 이벤트 Dead Letter Queue Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Queue stockDecreaseDeadLetterQueue() {
    return QueueBuilder.durable(stockProperties.getDecreaseDead().getQueue()).build();
  }

  /**
   * 재고 감소 이벤트 Dead Letter Queue Binding이다.
   *
   * <p>Dead Letter용 RoutingKey에 따라 메시지를 DLQ로 라우팅한다.
   *
   * @param stockDecreaseDeadLetterQueue 재고 감소 이벤트 Dead Letter Queue
   * @param stockExchange Stock Topic Exchange
   * @return 재고 감소 이벤트 Dead Letter Queue Binding Bean
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockDecreaseDeadLetterBinding(
      Queue stockDecreaseDeadLetterQueue, TopicExchange stockExchange) {

    return BindingBuilder.bind(stockDecreaseDeadLetterQueue)
        .to(stockExchange)
        .with(stockProperties.getDecreaseDead().getRoutingKey());
  }
}
