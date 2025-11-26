package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.order;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Order 서비스 관련 RabbitMQ 설정 정보를 바인딩하는 Properties 클래스이다.
 *
 * <p>{@code application.yml} 또는 {@code application-*.yml}에 정의된 {@code rabbit.order.*} 설정 값을 객체로
 * 매핑한다.
 *
 * <p>구조 예시:
 *
 * <pre>
 * rabbit:
 *   order:
 *     exchange: order.exchange
 *     created:
 *       queue: order.created.queue
 *       routing-key: order.created
 * </pre>
 *
 * <p>주요 설정 값:
 *
 * <ul>
 *   <li>{@code exchange} : 주문 관련 이벤트를 발행하는 Exchange 이름
 *   <li>{@code created.queue} : 주문 생성 이벤트 수신 Queue 이름
 *   <li>{@code created.routing-key} : 주문 생성 이벤트 Routing Key
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "rabbit.order")
public class RabbitOrderProperties {

  /** 주문 관련 이벤트를 발행/수신하는 Exchange 이름. */
  private String exchange;

  /** 주문 생성(OrderCreatedEvent) 관련 설정. */
  private Created created;

  /**
   * 주문 생성 이벤트 관련 설정이다.
   *
   * @author 김지원
   * @since 1.0.0
   */
  @Data
  public static class Created {

    /** 주문 생성 이벤트를 수신하는 Queue 이름. */
    private String queue;

    /** 주문 생성 이벤트용 Routing Key. */
    private String routingKey;
  }
}
