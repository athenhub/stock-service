package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import java.util.List;
import java.util.UUID;

/**
 * StockInitializeCommand 테스트용 픽스처.
 *
 * @author 김지원
 * @since 1.0.0
 */
public final class StockInitializeCommandFixture {

  /**
   * 기본 재고 초기화 커맨드를 생성한다.
   *
   * @return StockInitializeCommand.
   * @author 김지원
   * @since 1.0.0
   */
  public static StockInitializeCommand create() {
    UUID productId = UUID.randomUUID();

    return new StockInitializeCommand(
        productId,
        List.of(
            new StockInitializeCommand.ProductVariant(UUID.randomUUID(), 5),
            new StockInitializeCommand.ProductVariant(UUID.randomUUID(), 10)));
  }

  private StockInitializeCommandFixture() {}
}
