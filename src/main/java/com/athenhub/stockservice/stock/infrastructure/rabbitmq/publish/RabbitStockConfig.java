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
 * Stock 도메인 관련 RabbitMQ 설정 클래스이다.
 *
 * <p>재고 서비스에서 발생하는 이벤트를 외부로 발행하기 위해 Exchange, Queue, Binding 정보를 정의한다.
 *
 * <p>주요 역할:
 *
 * <ul>
 *   <li>Stock 이벤트용 Topic Exchange 생성
 *   <li>재고 등록 / 감소 이벤트용 Queue 생성
 *   <li>Exchange - Queue 간 Binding 설정
 * </ul>
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
   * Stock 관련 이벤트를 발행하는 Topic Exchange.
   *
   * <p>예: stock.exchange
   *
   * @return Stock 이벤트용 TopicExchange
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public TopicExchange stockExchange() {
    return new TopicExchange(
        stockProperties.getExchange(), // 예: stock.exchange
        true,
        false);
  }

  /**
   * 재고 등록 이벤트를 발행하기 위한 Queue.
   *
   * <p>예: stock.registered.queue
   *
   * @return 재고 등록 이벤트용 Queue
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Queue stockRegisteredQueue() {
    return QueueBuilder.durable(stockProperties.getRegistered().getQueue()).build();
  }

  /**
   * 재고 등록 이벤트용 Binding.
   *
   * <p>Exchange로 들어온 재고 등록 이벤트를 지정된 Queue로 라우팅한다.
   *
   * <p>※ 메서드 이름에 오타(registerd)가 있으나, Bean 등록에는 영향이 없어 현재는 그대로 둔다.
   *
   * @param stockRegisteredQueue 재고 등록 이벤트 Queue
   * @param stockExchange Stock Topic Exchange
   * @return 재고 등록 이벤트 Binding
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockRegisterdBinding(Queue stockRegisteredQueue, TopicExchange stockExchange) {
    return BindingBuilder.bind(stockRegisteredQueue)
        .to(stockExchange)
        .with(stockProperties.getRegistered().getRoutingKey());
  }

  /**
   * 재고 감소 이벤트를 발행하기 위한 Queue.
   *
   * <p>예: stock.decreased.queue
   *
   * @return 재고 감소 이벤트용 Queue
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Queue stockDecreasedQueue() {
    return QueueBuilder.durable(stockProperties.getDecreased().getQueue()).build();
  }

  /**
   * 재고 감소 이벤트용 Binding.
   *
   * <p>Exchange로 들어온 재고 감소 이벤트를 지정된 Queue로 라우팅한다.
   *
   * @param stockDecreasedQueue 재고 감소 이벤트 Queue
   * @param stockExchange Stock Topic Exchange
   * @return 재고 감소 이벤트 Binding
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockDecreasedBinding(Queue stockDecreasedQueue, TopicExchange stockExchange) {
    return BindingBuilder.bind(stockDecreasedQueue)
        .to(stockExchange)
        .with(stockProperties.getDecreased().getRoutingKey());
  }

  @Bean
  public Queue stockDecreaseFailQueue() {
    return QueueBuilder.durable(stockProperties.getDecreaseFail().getQueue()).build();
  }

  /**
   * 재고 감소 이벤트용 Binding.
   *
   * <p>Exchange로 들어온 재고 감소 이벤트를 지정된 Queue로 라우팅한다.
   *
   * @param stockDecreasedQueue 재고 감소 이벤트 Queue
   * @param stockExchange Stock Topic Exchange
   * @return 재고 감소 이벤트 Binding
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockDecreaseFailBinding(
      Queue stockDecreaseFailQueue, TopicExchange stockExchange) {
    return BindingBuilder.bind(stockDecreaseFailQueue)
        .to(stockExchange)
        .with(stockProperties.getDecreaseFail().getRoutingKey());
  }
}
