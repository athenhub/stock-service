package com.athenhub.stockservice.stock.application.listener;

import com.athenhub.stockservice.stock.domain.StockEventType;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StockHistoryEventListener {

  private final StockHistoryRepository stockHistoryRepository;

  @Transactional
  @EventListener
  public void onStockCreated(StockCreatedEvent event) {
    StockHistory history =
        StockHistory.create(
            event.quantity(),
            event.stockId(),
            event.productId(),
            event.variantId(),
            StockEventType.INBOUND);

    stockHistoryRepository.save(history);
  }
}
