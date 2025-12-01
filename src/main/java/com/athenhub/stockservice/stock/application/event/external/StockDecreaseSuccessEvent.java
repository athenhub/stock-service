package com.athenhub.stockservice.stock.application.event.external;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주문 처리로 인해 재고가 감소했음을 알리는 외부 이벤트이다.
 *
 * <p>특정 주문(Order)에 의해 재고 감소가 정상적으로 완료되었을 때 발행되며, 다른 바운디드 컨텍스트(예: Order 서비스, Notification 서비스 등)에
 * 전달된다.
 *
 * @param orderId 재고 감소를 발생시킨 주문 ID
 * @param decreasedAt 재고 감소가 완료된 시각
 * @author 김지원
 * @since 1.0.0
 */
public record StockDecreaseSuccessEvent(

    /* 재고 감소를 발생시킨 주문 ID. */
    UUID orderId,

    /* 재고 감소가 완료된 시각. */
    LocalDateTime decreasedAt) {

  /**
   * 현재 시각을 기준으로 재고 감소 이벤트를 생성한다.
   *
   * @param orderId 재고 감소를 발생시킨 주문 ID
   * @return 생성된 StockDecreasedEvent
   * @author 김지원
   * @since 1.0.0
   */
  public static StockDecreaseSuccessEvent of(UUID orderId) {
    return new StockDecreaseSuccessEvent(orderId, LocalDateTime.now());
  }
}
