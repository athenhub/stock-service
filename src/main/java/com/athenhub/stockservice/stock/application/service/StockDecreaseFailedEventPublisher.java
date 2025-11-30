package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.event.external.StockDecreaseFailedEvent;

public interface StockDecreaseFailedEventPublisher {
  void publish(StockDecreaseFailedEvent event);
}
