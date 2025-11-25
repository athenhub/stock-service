package com.athenhub.stockservice.stock.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockDecreaseRequest(
    @NotNull UUID productId,
    @NotNull UUID variantId,
    @Min(1) int quantity,
    @NotNull LocalDateTime requestAt) {}
