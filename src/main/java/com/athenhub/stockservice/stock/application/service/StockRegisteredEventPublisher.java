package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.domain.event.external.StockRegisteredEvent;

public interface StockRegisteredEventPublisher {

  void publish(StockRegisteredEvent event);
}
