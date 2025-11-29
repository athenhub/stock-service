package com.athenhub.stockservice.stock.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StockDecreaseBatchEvent(
    UUID orderId, LocalDateTime orderedAt, List<StockDecreaseRequest> stockDecreaseRequests) {}
