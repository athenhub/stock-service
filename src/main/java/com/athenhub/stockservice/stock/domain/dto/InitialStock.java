package com.athenhub.stockservice.stock.domain.dto;

import java.util.UUID;

public record InitialStock(UUID productId, UUID variantId, int quantity) {}
