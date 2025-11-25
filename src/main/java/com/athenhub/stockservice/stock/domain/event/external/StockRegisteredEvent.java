package com.athenhub.stockservice.stock.domain.event.external;

import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record StockRegisteredEvent(UUID productId, LocalDateTime requestAt) {

  public static StockRegisteredEvent from(StockCreatedEvent event) {
    Set<ProductId> productIds =
        event.stocks().stream()
            .map(StockCreatedEvent.StockInfo::productId)
            .collect(Collectors.toSet());

    if (productIds.size() != 1) {
      throw new IllegalStateException("하나의 StockCreatedEvent에는 하나의 productId만 들어 있어야 한다");
    }

    return new StockRegisteredEvent(productIds.iterator().next().toUuid(), event.requestAt());
  }
}
