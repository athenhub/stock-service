package com.athenhub.stockservice.stock.domain.dto;

import java.util.List;
import java.util.UUID;

public record ProductVariantMatchCommand(UUID productId, List<UUID> productVariantIds) {}
