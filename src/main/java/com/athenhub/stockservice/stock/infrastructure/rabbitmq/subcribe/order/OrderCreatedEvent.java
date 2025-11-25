package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID orderId, List<OrderedProduct> products, LocalDateTime orderedAt) {

  public record OrderedProduct(UUID productId, UUID variantId, int quantity) {}
}
