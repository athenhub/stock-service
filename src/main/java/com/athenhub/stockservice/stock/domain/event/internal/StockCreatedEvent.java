package com.athenhub.stockservice.stock.domain.event.internal;

import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import com.athenhub.stockservice.stock.domain.vo.StockId;

/**
 * 재고(Stock) 생성 시 발생하는 도메인 내부 이벤트이다.
 *
 * <p>Stock 엔티티가 최초로 생성될 때 발행되며, 다음과 같은 후속 처리를 트리거하는 데 사용된다.
 *
 * <ul>
 *   <li>재고 이력(StockHistory) 저장
 *   <li>외부 메시지(RabbitMQ) 발행용 이벤트로 변환
 * </ul>
 *
 * <p><b>주의:</b> 이 이벤트는 외부에 직접 노출되지 않고, 내부 흐름(Spring Event, Transactional Event)에만 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record StockCreatedEvent(
    StockId stockId, ProductId productId, ProductVariantId variantId, int quantity) {

  /**
   * Stock 엔티티로부터 StockCreatedEvent를 생성한다.
   *
   * @param stock 생성된 재고 엔티티
   * @return StockCreatedEvent
   */
  public static StockCreatedEvent from(Stock stock) {
    return new StockCreatedEvent(
        stock.getId(), stock.getProductId(), stock.getVariantId(), stock.getQuantity());
  }
}
