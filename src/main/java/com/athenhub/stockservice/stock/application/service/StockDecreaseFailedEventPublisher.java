package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.domain.event.external.StockDecreaseFailedEvent;

public interface StockDecreaseFailedEventPublisher {
  void publish(StockDecreaseFailedEvent event);
}
