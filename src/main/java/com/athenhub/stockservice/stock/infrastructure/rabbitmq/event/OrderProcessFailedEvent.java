package com.athenhub.stockservice.stock.infrastructure.rabbitmq.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주문 처리 과정(Order Saga Step) 중 실패가 발생했음을 나타내는 도메인 이벤트이다.
 *
 * <p>본 이벤트는 Stock 서비스가 주문 생성 이후 처리하는 단계(예: 재고 감소 처리)에서 오류가 발생했을 때 발행되며, Order 서비스는 이 이벤트를 기반으로 주문
 * 상태를 <strong>FAILED</strong> 또는 <strong>CANCELED</strong> 등으로 변경하거나 후속 보상 트랜잭션(결제 취소, 재고 롤백 등)을
 * 수행한다.
 *
 * <p>오류 유형(errorCode)은 시스템이 이해하는 식별자(예: OUT_OF_STOCK, RETRY_EXCEEDED)이며, errorMessage는
 * MessageResolver 등을 통해 변환된 사람이 읽을 수 있는 메시지다.
 *
 * @param orderId 실패가 발생한 주문 ID
 * @param errorCode 실패 유형 코드(시스템 식별 목적)
 * @param errorMessage 실패 상세 사유(사용자/운영자용 메시지)
 * @param failedAt 실패가 발생한 시각
 * @author 김지원
 * @since 1.0.0
 */
public record OrderProcessFailedEvent(
    UUID orderId, String errorCode, String errorMessage, LocalDateTime failedAt) {

  /**
   * 현재 시각을 기준으로 실패 이벤트를 생성하는 생성자이다.
   *
   * @param orderId 실패가 발생한 주문 ID
   * @param errorCode 실패 유형 코드
   * @param errorMessage 실패 상세 메시지
   */
  public OrderProcessFailedEvent(UUID orderId, String errorCode, String errorMessage) {
    this(orderId, errorCode, errorMessage, LocalDateTime.now());
  }
}
