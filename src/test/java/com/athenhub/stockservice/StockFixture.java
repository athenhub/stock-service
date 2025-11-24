package com.athenhub.stockservice;

import com.athenhub.stockservice.product.domain.Stock;
import com.athenhub.stockservice.product.domain.StockEventType;
import com.athenhub.stockservice.product.domain.StockHistory;
import com.athenhub.stockservice.product.domain.vo.ProductId;
import com.athenhub.stockservice.product.domain.vo.ProductVariantId;
import com.athenhub.stockservice.product.domain.vo.StockId;
import java.util.UUID;

/**
 * 재고(Stock) 및 재고 이력(StockHistory) 테스트 생성을 위한 픽스처 클래스이다.
 *
 * <p>테스트 코드에서 반복적인 객체 생성을 줄이고, 일관된 기본값을 제공하기 위해 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public class StockFixture {

  /**
   * 임의의 상품/옵션 정보를 가진 재고 객체를 생성한다.
   *
   * @param quantity 초기 재고 수량
   * @return Stock
   */
  public static Stock createStock(int quantity) {
    return Stock.of(
        quantity, ProductId.of(UUID.randomUUID()), ProductVariantId.of(UUID.randomUUID()));
  }

  /**
   * 임의의 식별자를 가진 재고 이력 객체를 생성한다.
   *
   * @param changedQuantity 변경된 재고 수량
   * @param eventType 재고 이벤트 유형
   * @return StockHistory
   */
  public static StockHistory createStockHistory(int changedQuantity, StockEventType eventType) {
    return StockHistory.of(
        changedQuantity,
        StockId.of(UUID.randomUUID()),
        ProductId.of(UUID.randomUUID()),
        ProductVariantId.of(UUID.randomUUID()),
        eventType);
  }
}
