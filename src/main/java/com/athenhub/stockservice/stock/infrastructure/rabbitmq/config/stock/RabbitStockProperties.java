package com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 재고(Stock) 서비스에서 사용하는 RabbitMQ 설정 값을 바인딩하는 Properties 클래스이다.
 *
 * <p>Spring Boot의 {@link ConfigurationProperties} 기능을 통해 {@code application.yml}의 {@code
 * rabbit.stock.*} 아래에 정의된 값을 타입 안전하게 로딩한다.
 *
 * <p>본 설정은 재고 서비스가 발행하거나 수신하는 주요 메시지 흐름을 다음과 같이 관리한다:
 *
 * <ul>
 *   <li>{@code stock.exchange} — 재고 관련 이벤트가 오가는 Exchange
 *   <li>{@code stock.registered.*} — 재고 신규 등록 이벤트 처리
 *   <li>{@code stock.decrease.*} — 재고 감소(메인 큐) 이벤트 처리
 *   <li>{@code stock.decreased-retry.*} — 재고 감소 실패 시 재시도(Retry Queue) 처리
 *   <li>{@code stock.decreased-dead.*} — 재고 감소 5회 실패 시 최종 격리(DLQ)
 *   <li>{@code stock.decrease-success.*} — 재고 감소 성공 후 후처리 이벤트
 * </ul>
 *
 * <p>예시 YAML 구조:
 *
 * <pre>
 * rabbit:
 *   stock:
 *     exchange: stock.exchange
 *
 *     registered:
 *       queue: stock.registered.queue
 *       routing-key: stock.registered
 *
 *     decrease:
 *       queue: stock.decreased.queue
 *       routing-key: stock.decreased
 *
 *     decreased-retry:
 *       queue: stock.decreased.retry.queue
 *       routing-key: stock.decreased.retry
 *       ttl: 5000
 *
 *     decrease-dead:
 *       queue: stock.decreased.dead.queue
 *       routing-key: stock.decreased.dead
 *
 *     decrease-success:
 *       queue: stock.decreased.success.queue
 *       routing-key: stock.decreased.success
 * </pre>
 *
 * <p>RabbitStockProperties는 Stock 서비스의 메시징 레이어 전체를 관리하는 핵심 설정 클래스이며, Exchange–Queue–RoutingKey를 한
 * 눈에 파악할 수 있도록 구조화되어 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "rabbit.stock")
public class RabbitStockProperties {

  /** 재고 관련 메시지를 발행/수신하기 위한 Exchange 이름(예: stock.exchange). */
  private String exchange;

  /** 재고 감소 실패 메시지를 최종적으로 격리하기 위한 DLQ 전용 Exchange. */
  private String dlqExchange;

  /** 재고 등록(StockRegisteredEvent) 이벤트 설정 그룹. */
  private Registered registered;

  /** 재고 감소(StockDecreaseEvent, Main Queue) 이벤트 설정 그룹. */
  private Decrease decrease;

  /** 재고 감소 성공(StockDecreaseSuccessEvent) 이벤트 설정 그룹. */
  private DecreaseSuccess decreaseSuccess;

  /**
   * 재고 감소 실패 시 일정 시간 후 재처리하기 위한 Retry Queue 설정.
   *
   * <p>Retry Queue는 DLX(Dead-Letter Exchange)와 TTL(Time-To-Live)을 조합해 "지연 재시도(Delayed Retry)" 기능을
   * 제공한다.
   */
  private DecreaseRetry decreaseRetry;

  /**
   * 재고 감소가 5회 실패한 경우 최종 격리되는 Dead Letter Queue 설정.
   *
   * <p>DLQ는 비즈니스적 개입이 필요한 메시지를 보관하며, 운영자가 Slack 알림 / 모니터링 시스템 등으로 확인할 수 있도록 활용된다.
   */
  private DecreaseDead decreaseDead;

  /**
   * 재고 등록 이벤트 관련 Queue / RoutingKey 설정이다.
   *
   * <p>예: stock.registered.queue / stock.registered
   */
  @Data
  public static class Registered {

    /** 재고 등록 이벤트를 수신하는 Queue 이름. */
    private String queue;

    /** 재고 등록 이벤트를 라우팅하기 위한 Routing Key. */
    private String routingKey;
  }

  /**
   * 재고 감소 이벤트(Main Queue) 관련 설정이다.
   *
   * <p>OrderCreatedEvent → StockDecreaseBatchEvent 를 받아 실제 재고 감소를 수행한다.
   */
  @Data
  public static class Decrease {

    /** 재고 감소(Main) 이벤트를 처리하는 Queue. */
    private String queue;

    /** 재고 감소(Main) 이벤트를 라우팅하는 Routing Key. */
    private String routingKey;
  }

  /**
   * 재고 감소 실패 시 지연 재시도를 위한 Retry Queue 설정이다.
   *
   * <p>Retry Queue는 다음과 같은 구조로 동작한다:
   *
   * <ol>
   *   <li>Main Queue 처리 실패 → Reject → DLX로 이동
   *   <li>Retry Queue에서 TTL 동안 대기
   *   <li>TTL 만료 후 DLX 재작동 → Main Queue 재유입
   * </ol>
   *
   * <p>RoutingKey는 반드시 `<domain>.<action>.retry` 형식을 따른다.
   */
  @Data
  public static class DecreaseRetry {

    /** 재고 감소 Retry Queue 이름. */
    private String queue;

    /** Retry Queue 라우팅키. */
    private String routingKey;

    /** 메시지가 재시도 전 대기할 TTL(ms). */
    private int ttl;
  }

  /**
   * 재고 감소가 5회 실패하여 최종 격리되는 Dead Letter Queue 설정이다.
   *
   * <p>DLQ로 들어간 메시지는 자동 재처리되지 않으며, 운영자 또는 보상 트랜잭션 시스템에 의해 확인·처리된다.
   */
  @Data
  public static class DecreaseDead {

    /** Dead Letter Queue 이름. */
    private String queue;

    /** Dead Letter Queue 라우팅키. */
    private String routingKey;
  }

  /**
   * 재고 감소가 정상적으로 완료되었을 때 발행되는 성공 이벤트용 설정이다.
   *
   * <p>예: 상품 상세 페이지 캐시 갱신, ElasticSearch 재색인 등 후처리가 필요한 경우 사용한다.
   */
  @Data
  public static class DecreaseSuccess {

    /** 재고 감소 성공 이벤트 Queue 이름. */
    private String queue;

    /** 재고 감소 성공 이벤트 Routing Key. */
    private String routingKey;
  }
}
