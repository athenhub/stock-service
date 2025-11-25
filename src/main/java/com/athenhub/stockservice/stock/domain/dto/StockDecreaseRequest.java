package com.athenhub.stockservice.stock.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockDecreaseRequest(
    UUID productId, UUID variantId, int quantity, LocalDateTime requestAt) {}
