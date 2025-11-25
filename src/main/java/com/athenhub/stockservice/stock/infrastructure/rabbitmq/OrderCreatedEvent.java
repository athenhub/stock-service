package com.athenhub.stockservice.stock.infrastructure.rabbitmq;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID orderId,
    UUID productId, UUID variantId, int quantity, LocalDateTime orderedAt
) {}
