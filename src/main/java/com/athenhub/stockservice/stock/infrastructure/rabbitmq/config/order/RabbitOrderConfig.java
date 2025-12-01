package com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.order;

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
 * Order 도메인에서 사용하는 RabbitMQ Exchange, Queue, Binding 설정을 정의하는 구성 클래스이다.
 *
 * <p>{@link RabbitOrderProperties}의 설정 값을 기반으로 주문 생성 이벤트 및 주문 처리 실패 이벤트(OrderProcessFailedEvent)를
 * 수신하기 위한 메시징 인프라를 등록한다.
 *
 * <p>주문 생성 이벤트는 재고 감소 요청 등의 후속 프로세스를 시작하는 역할을 하며, 주문 처리 실패 이벤트는 재고·결제·배송 등 외부 도메인의 실패를 Order 서비스가
 * 수신하여 보상 트랜잭션 또는 주문 상태 전환을 수행하도록 돕는다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitOrderProperties.class)
public class RabbitOrderConfig {

  private final RabbitOrderProperties orderProperties;

  /**
   * Order 서비스 이벤트를 발행/수신하기 위한 Topic Exchange를 생성한다.
   *
   * @return 주문 관련 이벤트용 TopicExchange.
   */
  @Bean
  public TopicExchange orderExchange() {
    return new TopicExchange(orderProperties.getExchange(), true, false);
  }

  /**
   * 주문 생성 이벤트(OrderCreatedEvent)를 수신하는 Queue를 생성한다.
   *
   * @return 주문 생성 이벤트용 Queue.
   */
  @Bean
  public Queue orderCreatedQueue() {
    return QueueBuilder.durable(orderProperties.getCreated().getQueue()).build();
  }

  /**
   * 주문 생성 이벤트 Queue와 Exchange를 Routing Key 기반으로 Binding한다.
   *
   * @return 주문 생성 이벤트용 Binding.
   */
  @Bean
  public Binding orderCreateBinding() {
    return BindingBuilder.bind(orderCreatedQueue())
        .to(orderExchange())
        .with(orderProperties.getCreated().getRoutingKey());
  }

  /**
   * 주문 처리 실패 이벤트(OrderProcessFailedEvent)를 수신하는 Queue를 생성한다.
   *
   * <p>해당 Queue는 재고 감소 실패뿐 아니라 결제 실패, 배송 실패 등 전체 주문 처리 플로우에서 발생한 모든 도메인 실패 이벤트를 수신한다.
   *
   * @return 주문 처리 실패 이벤트용 Queue.
   */
  @Bean
  public Queue orderProcessFailQueue() {
    return QueueBuilder.durable(orderProperties.getProcessFailed().getQueue()).build();
  }

  /**
   * 주문 처리 실패 이벤트 Queue와 Exchange를 Routing Key 기반으로 Binding한다.
   *
   * @return 주문 처리 실패 이벤트용 Binding.
   */
  @Bean
  public Binding orderProcessFailBinding() {
    return BindingBuilder.bind(orderProcessFailQueue())
        .to(orderExchange())
        .with(orderProperties.getProcessFailed().getRoutingKey());
  }
}
