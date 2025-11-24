package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.global.infrastructure.springevent.Events;
import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.dto.RegisterRequest;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.service.BelongsToValidator;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterStockService {

  private final ProductAccessPermissionValidator permissionValidator;
  private final BelongsToValidator belongsToValidator;

  private final StockRepository stockRepository;

  public RegisterResponse register(RegisterRequest request, AccessContext accessContext) {
    Stock stock = Stock.of(request, accessContext, belongsToValidator, permissionValidator);
    stockRepository.save(stock);

    Events.trigger(StockCreatedEvent.from(stock));

    return new RegisterResponse(stock.getId().toUuid());
  }
}
