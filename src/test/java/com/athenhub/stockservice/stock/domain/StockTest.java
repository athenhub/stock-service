package com.athenhub.stockservice.stock.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.athenhub.stockservice.stock.domain.dto.InitialStock;
import com.athenhub.stockservice.stock.domain.exception.InsufficientStockException;
import com.athenhub.stockservice.stock.fixture.StockFixture;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockTest {

  @DisplayName("모든 조건을 만족하면 Stock이 정상적으로 생성된다.")
  @Test
  void create_success() {
    // given
    InitialStock request = new InitialStock(UUID.randomUUID(), UUID.randomUUID(), 10);

    // when
    Stock stock = Stock.create(request);

    // then
    assertThat(stock).isNotNull();
    assertThat(stock.getQuantity()).isEqualTo(request.quantity());
    assertThat(stock.getProductId()).isNotNull();
    assertThat(stock.getVariantId()).isNotNull();
  }

  @Test
  void create_negative_quantity() {
    assertThatThrownBy(() -> StockFixture.createWithQuantity(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("재고 수량은 1 이상입니다.");
  }

  @Test
  void increase() {
    Stock stock = StockFixture.createWithQuantity(10);
    stock.increase(10);
    assertThat(stock.getQuantity()).isEqualTo(20);
  }

  @Test
  void increase_negative_value() {
    Stock stock = StockFixture.createWithQuantity(10);

    assertThatThrownBy(() -> stock.increase(-10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("증가 수량은 1 이상이어야 합니다.");
  }

  @Test
  void decrease() {
    Stock stock = StockFixture.createWithQuantity(10);
    stock.decrease(5);
    Assertions.assertThat(stock.getQuantity()).isEqualTo(5);
  }

  @Test
  void decrease_negative_value() {
    Stock stock = StockFixture.createWithQuantity(10);

    assertThatThrownBy(() -> stock.decrease(-10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("감소 수량은 1 이상이어야 합니다.");
  }

  @Test
  void decrease_gt_quantity() {
    Stock stock = StockFixture.createWithQuantity(10);

    assertThatThrownBy(() -> stock.decrease(11))
        .isInstanceOf(InsufficientStockException.class)
        .hasMessage("재고가 부족합니다.");
  }
}
