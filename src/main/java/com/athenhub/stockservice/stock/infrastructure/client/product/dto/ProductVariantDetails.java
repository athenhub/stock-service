package com.athenhub.stockservice.stock.infrastructure.client.product.dto;

import java.util.UUID;

public record ProductVariantDetails(UUID variantId, String color, String size, int quantity) {}
