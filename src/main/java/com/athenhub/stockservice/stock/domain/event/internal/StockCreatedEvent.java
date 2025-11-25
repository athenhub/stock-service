package com.athenhub.stockservice.stock.domain.event.internal;

import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import com.athenhub.stockservice.stock.domain.vo.StockId;
import java.time.LocalDateTime;
import java.util.List;

public record StockCreatedEvent(List<StockInfo> stocks, LocalDateTime requestAt) {

  public record StockInfo(
      StockId stockId, ProductId productId, ProductVariantId variantId, int quantity) {}

  public static StockCreatedEvent from(List<Stock> stocks) {
    List<StockInfo> stockInfos =
        stocks.stream()
            .map(
                it ->
                    new StockInfo(
                        it.getId(), it.getProductId(), it.getVariantId(), it.getQuantity()))
            .toList();
    return new StockCreatedEvent(stockInfos, LocalDateTime.now());
  }
}
