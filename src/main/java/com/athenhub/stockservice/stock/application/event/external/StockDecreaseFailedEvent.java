package com.athenhub.stockservice.stock.application.event.external;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재고 감소 처리 실패 시 외부 도메인(예: 주문 서비스)로 전달되는 이벤트이다.
 *
 * <p>재고 감소 실패는 재고 부족, 낙관적 락 충돌, 시스템 오류 등 다양한 원인으로 발생할 수 있으며, 본 이벤트는 실패 사유와 함께 주문 ID를 전달하여 주문 서비스가
 * 보상 트랜잭션(주문 취소, 결제 취소 등)을 수행할 수 있도록 한다.
 *
 * <p>이 이벤트는 재고 서비스의 내부 처리 실패 시 발행되며, {@code failedAt} 필드를 통해 실패가 발생한 시점을 명확하게 기록한다.
 *
 * @param orderId 재고 감소 실패가 발생한 주문의 ID
 * @param message 실패 사유(사용자 또는 운영자가 이해할 수 있는 실패 메시지)
 * @param failedAt 실패가 발생한 시각
 * @author 김지원
 * @since 1.0.0
 */
public record StockDecreaseFailedEvent(UUID orderId, String message, LocalDateTime failedAt) {

  /**
   * 현재 시점을 기준으로 실패 이벤트를 생성하는 팩토리 메서드이다.
   *
   * @param orderId 재고 감소 실패가 발생한 주문의 ID
   * @param message 실패 사유 메시지
   * @return 생성된 {@link StockDecreaseFailedEvent} 인스턴스
   */
  public static StockDecreaseFailedEvent of(UUID orderId, String message) {
    return new StockDecreaseFailedEvent(orderId, message, LocalDateTime.now());
  }
}
