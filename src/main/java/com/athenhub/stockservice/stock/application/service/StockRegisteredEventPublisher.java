package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.event.external.StockRegisteredEvent;

/**
 * 재고 등록 완료 이벤트를 발행하는 Publisher 인터페이스이다.
 *
 * <p>상품에 대한 재고가 최초로 등록되었음을 외부 시스템(예: Product 서비스)에 알리기 위해 사용된다.
 *
 * <p>구현체는 메시지 브로커(RabbitMQ, Kafka 등) 또는 애플리케이션 이벤트 등 다양한 방식으로 확장될 수 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface StockRegisteredEventPublisher {

  /**
   * 재고 등록 완료 이벤트를 발행한다.
   *
   * @param event 재고 등록 완료 이벤트
   * @author 김지원
   * @since 1.0.0
   */
  void publish(StockRegisteredEvent event);
}
