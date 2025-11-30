package com.athenhub.stockservice.stock.application.event.external;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockDecreaseFailedEvent(UUID orderId, String message, LocalDateTime failedAt) {

  public static StockDecreaseFailedEvent of(UUID orderId, String message) {
    return new StockDecreaseFailedEvent(orderId, message, LocalDateTime.now());
  }
}
