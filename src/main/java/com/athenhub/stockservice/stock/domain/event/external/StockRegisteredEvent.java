package com.athenhub.stockservice.stock.domain.event.external;

import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import java.util.UUID;

public record StockRegisteredEvent(
    UUID stockId, UUID productId, UUID productVariantId, int quantity) {
  public static StockRegisteredEvent from(StockCreatedEvent event) {
    return new StockRegisteredEvent(
        event.stockId().toUuid(),
        event.productId().toUuid(),
        event.variantId().toUuid(),
        event.quantity());
  }
}
