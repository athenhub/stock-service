package com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.order;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Order 서비스에서 사용되는 RabbitMQ 관련 설정을 바인딩하는 Properties 클래스이다.
 *
 * <p>본 클래스는 {@code rabbit.order.*} 계층 아래 정의된 Exchange, Queue, Routing Key 정보를 객체 형태로 매핑하여 Order
 * 서비스가 사용하는 메시징 인프라 설정을 중앙에서 관리한다.
 *
 * <p>주요 역할:
 *
 * <ul>
 *   <li>주문 생성 이벤트(OrderCreatedEvent) 소비를 위한 Queue/RoutingKey 제공
 *   <li>주문 처리 실패 이벤트(OrderProcessFailedEvent) 소비를 위한 Queue/RoutingKey 제공
 * </ul>
 *
 * <p>application.yml 예시:
 *
 * <pre>
 * rabbit:
 *   order:
 *     exchange: order.exchange
 *     created:
 *       queue: order.created.queue
 *       routing-key: order.created
 *     process-failed:
 *       queue: order.process.failed.queue
 *       routing-key: order.process.failed
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "rabbit.order")
public class RabbitOrderProperties {

  /** 주문 관련 이벤트를 발행/수신하는 Exchange 이름. */
  private String exchange;

  /** 주문 생성(OrderCreatedEvent) 관련 Queue 및 Routing Key 설정. */
  private Created created;

  /** 주문 처리 실패(OrderProcessFailedEvent) 관련 Queue 및 Routing Key 설정. */
  private ProcessFailed processFailed;

  /**
   * 주문 생성 이벤트(OrderCreatedEvent) 수신을 위한 RabbitMQ 설정 값이다.
   *
   * <p>Order 서비스는 본 Queue를 통해 주문 생성 이벤트를 받아 재고 감소 요청 발행 등 주문 처리 프로세스의 첫 단계를 수행한다.
   *
   * @author 김지원
   * @since 1.0.0
   */
  @Data
  public static class Created {

    /** 주문 생성 이벤트를 수신하는 Queue 이름. */
    private String queue;

    /** 주문 생성 이벤트를 라우팅하는 Routing Key. */
    private String routingKey;
  }

  /**
   * 주문 처리 실패 이벤트(OrderProcessFailedEvent) 수신을 위한 RabbitMQ 설정 값이다.
   *
   * <p>Stock, Payment, Shipping 등 외부 도메인에서 주문 처리 중 발생한 모든 실패가 본 Queue/RoutingKey를 통해 Order 서비스로
   * 전달된다.
   *
   * <p>Order 서비스는 해당 이벤트를 기반으로 주문 상태를 실패/취소로 변경하거나 보상 트랜잭션을 수행하는 등 후속 처리를 담당한다.
   *
   * <p>본 설정은 재고 감소 실패뿐 아니라 향후 결제 실패, 배송 실패 등 모든 프로세스 실패를 통합적으로 처리하기 위한 구조이다.
   *
   * @author 김지원
   * @since 1.0.0
   */
  @Data
  public static class ProcessFailed {

    /** 주문 처리 실패 이벤트를 수신하는 Queue 이름. */
    private String queue;

    /** 주문 처리 실패 이벤트를 라우팅하는 Routing Key. */
    private String routingKey;
  }
}
