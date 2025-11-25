package com.athenhub.stockservice.stock.application.dto;

import java.util.List;
import java.util.UUID;

public record StockInitializeCommand(UUID productId, List<ProductVariant> productVariants) {
  public record ProductVariant(UUID id, int quantity) {}
}
