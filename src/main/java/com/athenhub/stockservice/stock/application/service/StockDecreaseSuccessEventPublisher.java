package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.event.external.StockDecreaseSuccessEvent;

/**
 * 재고 감소 이벤트를 발행하는 Publisher 인터페이스이다.
 *
 * <p>재고가 감소하는 비즈니스 로직이 성공적으로 처리된 이후, 해당 결과를 외부 시스템이나 다른 바운디드 컨텍스트에 알리기 위해 사용된다.
 *
 * <p>구현체는 메시지 브로커(RabbitMQ, Kafka 등) 또는 애플리케이션 이벤트 등 다양한 방식으로 확장될 수 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface StockDecreaseSuccessEventPublisher {

  /**
   * 재고 감소 이벤트를 발행한다.
   *
   * @param event 재고 감소 이벤트
   * @author 김지원
   * @since 1.0.0
   */
  void publish(StockDecreaseSuccessEvent event);
}
