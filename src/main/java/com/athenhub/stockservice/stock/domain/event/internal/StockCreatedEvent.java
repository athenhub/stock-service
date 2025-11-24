package com.athenhub.stockservice.stock.domain.event.internal;

import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import com.athenhub.stockservice.stock.domain.vo.StockId;

public record StockCreatedEvent(
    StockId stockId, ProductId productId, ProductVariantId variantId, int quantity) {
  public static StockCreatedEvent from(Stock stock) {
    return new StockCreatedEvent(
        stock.getId(), stock.getProductId(), stock.getVariantId(), stock.getQuantity());
  }
}
