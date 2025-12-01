package com.athenhub.stockservice.stock.application.event.external;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재고 등록 완료를 외부로 알리기 위한 이벤트이다.
 *
 * <p>상품에 대한 모든 재고가 정상적으로 등록된 이후 발행되며, 다른 바운디드 컨텍스트(예: Product 서비스)에서 이를 구독하여 후속 처리를 수행할 수 있도록 한다.
 *
 * @param productId 재고가 등록된 상품 ID
 * @param requestAt 이벤트 발생 시각
 * @author 김지원
 * @since 1.0.0
 */
public record StockRegisteredEvent(

    /** 재고가 등록된 상품 ID. */
    UUID productId,

    /** 이벤트 발생 시각. */
    LocalDateTime requestAt) {

  /**
   * 현재 시각을 기준으로 재고 등록 이벤트를 생성한다.
   *
   * @param productId 재고가 등록된 상품 ID
   * @return 생성된 StockRegisteredEvent
   * @author 김지원
   * @since 1.0.0
   */
  public static StockRegisteredEvent from(UUID productId) {
    return new StockRegisteredEvent(productId, LocalDateTime.now());
  }
}
