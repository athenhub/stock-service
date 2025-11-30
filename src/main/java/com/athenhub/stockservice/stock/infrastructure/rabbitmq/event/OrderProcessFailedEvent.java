package com.athenhub.stockservice.stock.infrastructure.rabbitmq.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주문 처리 과정(Order Saga Step) 중 실패가 발생했음을 나타내는 도메인 이벤트이다.
 *
 * <p>주로 OrderCreatedEvent 처리(재고 감소 요청 생성) 실패 시 Stock 서비스에서 발행하여 Order 서비스가 해당 실패 상태를 기반으로 주문 상태 변경,
 * 보상 트랜잭션 실행 등을 수행한다.
 *
 * @param orderId 실패가 발생한 주문 ID
 * @param errorCode 실패 유형을 나타내는 코드 문자열
 * @param errorMessage 실패 상세 사유
 * @param failedAt 실패가 발생한 시각
 * @author 김지원
 * @since 1.0.0
 */
public record OrderProcessFailedEvent(
    UUID orderId, String errorCode, String errorMessage, LocalDateTime failedAt) {

  public OrderProcessFailedEvent(UUID orderId, String errorCode, String errorMessage) {
    this(orderId, errorCode, errorMessage, LocalDateTime.now());
  }
}
