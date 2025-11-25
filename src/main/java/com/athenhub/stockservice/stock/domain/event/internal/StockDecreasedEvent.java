package com.athenhub.stockservice.stock.domain.event.internal;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockDecreasedEvent(UUID orderId, LocalDateTime decreasedAt) {

  public static StockDecreasedEvent of(UUID orderId) {
    return new StockDecreasedEvent(orderId, LocalDateTime.now());
  }
}
