package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Stock 서비스의 RabbitMQ 설정 정보를 바인딩하는 Properties 클래스이다.
 *
 * <p>{@code application.yml} 또는 {@code application-*.yml}에 정의된 {@code rabbit.stock.*} 설정 값을 객체로
 * 매핑한다.
 *
 * <p>구조 예시:
 *
 * <pre>
 * rabbit:
 *   stock:
 *     exchange: stock.exchange
 *     registered:
 *       queue: stock.registered.queue
 *       routing-key: stock.registered
 *     decreased:
 *       queue: stock.decreased.queue
 *       routing-key: stock.decreased
 *     decreased-fail:
 *       queue: stock.decreased.fail.queue
 *       routing-key: stock.decreased.fail
 * </pre>
 *
 * <p>주요 설정 값:
 *
 * <ul>
 *   <li>{@code exchange} : 재고 관련 이벤트용 Exchange 이름
 *   <li>{@code registered.queue} : 재고 등록 이벤트 Queue 이름
 *   <li>{@code registered.routing-key} : 재고 등록 이벤트 Routing Key
 *   <li>{@code decreased.queue} : 재고 감소 이벤트 Queue 이름
 *   <li>{@code decreased.routing-key} : 재고 감소 이벤트 Routing Key
 *   <li>{@code decreased-fail.queue} : 재고 감소 실패 이벤트 Queue 이름
 *   <li>{@code decreased-fail.routing-key} : 재고 감소 실패 이벤트 Routing Key
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "rabbit.stock")
public class RabbitStockProperties {

  /** 재고 관련 이벤트를 발행/수신하는 Exchange 이름. */
  private String exchange;

  /** 재고 등록(StockRegisteredEvent) 관련 설정. */
  private Registered registered;

  /** 재고 감소(StockDecreasedEvent) 관련 설정. */
  private Decreased decreased;

  /** 재고 감소 실패(StockDecreaseFailedEvent) 관련 설정. */
  private DecreaseFail decreaseFail;

  /**
   * 재고 등록 이벤트 관련 설정이다.
   *
   * @author 김지원
   * @since 1.0.0
   */
  @Data
  public static class Registered {

    /** 재고 등록 이벤트를 처리하는 Queue 이름. */
    private String queue;

    /** 재고 등록 이벤트용 Routing Key. */
    private String routingKey;
  }

  /**
   * 재고 감소 실패 이벤트 관련 설정이다.
   *
   * @author 김지원
   * @since 1.0.0
   */
  @Data
  public static class DecreaseFail {

    /** 재고 감소 실패 이벤트를 처리하는 Queue 이름. */
    private String queue;

    /** 재고 감소 실패 이벤트용 Routing Key. */
    private String routingKey;
  }

  /**
   * 재고 감소 이벤트 관련 설정이다.
   *
   * @author 김지원
   * @since 1.0.0
   */
  @Data
  public static class Decreased {

    /** 재고 감소 이벤트를 처리하는 Queue 이름. */
    private String queue;

    /** 재고 감소 이벤트용 Routing Key. */
    private String routingKey;
  }
}
