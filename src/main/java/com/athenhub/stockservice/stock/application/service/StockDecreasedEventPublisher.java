package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.domain.event.internal.StockDecreasedEvent;

public interface StockDecreasedEventPublisher {
  void publish(StockDecreasedEvent event);
}
