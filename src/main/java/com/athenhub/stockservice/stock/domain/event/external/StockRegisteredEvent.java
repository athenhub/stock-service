package com.athenhub.stockservice.stock.domain.event.external;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockRegisteredEvent(UUID productId, LocalDateTime requestAt) {

  public static StockRegisteredEvent from(UUID productId) {
    return new StockRegisteredEvent(productId, LocalDateTime.now());
  }
}
