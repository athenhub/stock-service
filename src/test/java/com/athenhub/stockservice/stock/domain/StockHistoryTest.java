package com.athenhub.stockservice.stock.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.athenhub.stockservice.stock.domain.vo.ProductId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import com.athenhub.stockservice.stock.domain.vo.StockId;
import com.athenhub.stockservice.stock.fixture.StockHistoryFixture;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * StockHistory 도메인 테스트.
 *
 * @author 김지원
 * @since 1.0.0
 */
class StockHistoryTest {

  @Test
  @DisplayName("재고가 변하면 재고 이력을 쌓는다.")
  void create_success() {
    // given
    int changedQuantity = -10;
    StockId stockId = StockId.of(UUID.randomUUID());
    ProductId productId = ProductId.of(UUID.randomUUID());
    ProductVariantId variantId = ProductVariantId.of(UUID.randomUUID());
    StockEventType eventType = StockEventType.OUTBOUND;

    // when
    StockHistory history =
        StockHistory.create(changedQuantity, stockId, productId, variantId, eventType);

    // then
    assertThat(history.getId()).isNotNull();
    assertThat(history.getStockId()).isEqualTo(stockId);
    assertThat(history.getChangedQuantity()).isEqualTo(changedQuantity);
    assertThat(history.getProductId()).isEqualTo(productId);
    assertThat(history.getVariantId()).isEqualTo(variantId);
    assertThat(history.getEventType()).isEqualTo(eventType);
  }

  @Test
  @DisplayName("입고(INBOUND)는 양수 수량으로 생성된다")
  void createInboundStockHistory_success() {
    // given
    StockHistory history = StockHistoryFixture.createStockHistory(10, StockEventType.INBOUND);

    // then
    assertThat(history.getChangedQuantity()).isEqualTo(10);
    assertThat(history.getEventType()).isEqualTo(StockEventType.INBOUND);
  }

  @Test
  @DisplayName("출고(OUTBOUND)는 음수 수량으로 생성된다")
  void createOutboundStockHistory_success() {
    // given
    StockHistory history = StockHistoryFixture.createStockHistory(-5, StockEventType.OUTBOUND);

    // then
    assertThat(history.getChangedQuantity()).isEqualTo(-5);
    assertThat(history.getEventType()).isEqualTo(StockEventType.OUTBOUND);
  }

  @Test
  @DisplayName("CANCEL(주문 취소)은 양수 수량만 허용된다")
  void createReturnStockHistory_success() {
    StockHistory history = StockHistoryFixture.createStockHistory(10, StockEventType.CANCEL);

    assertThat(history.getChangedQuantity()).isEqualTo(10);
    assertThat(history.getEventType()).isEqualTo(StockEventType.CANCEL);
  }

  @Test
  @DisplayName("수량이 0이면 예외가 발생한다")
  void throwException_whenQuantityIsZero() {
    assertThatThrownBy(() -> StockHistoryFixture.createStockHistory(0, StockEventType.INBOUND))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("변경하려는 재고 수량은 0이 될 수 없습니다.");
  }

  @Test
  @DisplayName("INBOUND에 음수 수량을 넣으면 예외가 발생한다")
  void throwException_whenInboundHasNegativeQuantity() {
    assertThatThrownBy(() -> StockHistoryFixture.createStockHistory(-3, StockEventType.INBOUND))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("입고는 양수 수량만 가능합니다.");
  }

  @Test
  @DisplayName("CANCEL에 음수 수량을 넣으면 예외가 발생한다")
  void throwException_whenCancelHasNegativeQuantity() {
    assertThatThrownBy(() -> StockHistoryFixture.createStockHistory(-3, StockEventType.CANCEL))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("주문 취소는 양수 수량만 가능합니다.");
  }

  @Test
  @DisplayName("OUTBOUND에 양수 수량을 넣으면 예외가 발생한다")
  void throwException_whenOutboundHasPositiveQuantity() {
    assertThatThrownBy(() -> StockHistoryFixture.createStockHistory(10, StockEventType.OUTBOUND))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("출고는 음수 수량만 가능합니다.");
  }

  @Test
  @DisplayName("EventType이 null이면 예외가 발생한다")
  void throwException_whenEventTypeIsNull() {
    assertThatThrownBy(() -> StockHistoryFixture.createStockHistory(10, null))
        .isInstanceOf(NullPointerException.class);
  }
}
