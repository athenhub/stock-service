package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID orderId,
    UUID productId, UUID variantId, int quantity, LocalDateTime orderedAt
) {}
