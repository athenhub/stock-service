package com.athenhub.stockservice.product.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.athenhub.stockservice.StockFixture;
import com.athenhub.stockservice.product.domain.vo.ProductId;
import com.athenhub.stockservice.product.domain.vo.ProductVariantId;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StockTest {

  @Test
  void create() {
    ProductId productId = ProductId.of(UUID.randomUUID());
    ProductVariantId variantId = ProductVariantId.of(UUID.randomUUID());

    Stock stock = Stock.of(10, productId, variantId);

    assertThat(stock.getId()).isNotNull();
    assertThat(stock.getQuantity()).isEqualTo(10);
    assertThat(stock.getProductId()).isEqualTo(productId);
    assertThat(stock.getVariantId()).isEqualTo(variantId);
  }

  @Test
  void create_negative_quantity() {
    assertThatThrownBy(() -> StockFixture.createStock(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("재고 수량은 1 이상입니다.");
  }

  @Test
  void increase() {
    Stock stock = StockFixture.createStock(10);
    stock.increase(10);
    Assertions.assertThat(stock.getQuantity()).isEqualTo(20);
  }

  @Test
  void increase_negative_value() {
    Stock stock = StockFixture.createStock(10);

    assertThatThrownBy(() -> stock.increase(-10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("증가 수량은 1 이상이어야 합니다.");
  }

  @Test
  void decrease() {
    Stock stock = StockFixture.createStock(10);
    stock.decrease(5);
    Assertions.assertThat(stock.getQuantity()).isEqualTo(5);
  }

  @Test
  void decrease_negative_value() {
    Stock stock = StockFixture.createStock(10);

    assertThatThrownBy(() -> stock.decrease(-10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("감소 수량은 1 이상이어야 합니다.");
  }

  @Test
  void decrease_gt_quantity() {
    Stock stock = StockFixture.createStock(10);

    assertThatThrownBy(() -> stock.decrease(11))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("재고가 부족합니다.");
  }
}
