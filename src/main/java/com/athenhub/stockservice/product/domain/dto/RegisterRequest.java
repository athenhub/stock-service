package com.athenhub.stockservice.product.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegisterRequest(
    @NotNull UUID productId, @NotNull UUID variantId, @Min(1) int quantity) {}
