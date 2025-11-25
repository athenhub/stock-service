package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.global.infrastructure.springevent.Events;
import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import com.athenhub.stockservice.stock.application.exception.ApplicationErrorCode;
import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.dto.InitialStock;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterStockService {

  private final StockRepository stockRepository;

  public RegisterResponse register(StockInitializeCommand command) {
    validateUniqueVariants(command);

    List<Stock> stocks = convertToStocks(command);
    stockRepository.saveAll(stocks);

    Events.trigger(StockCreatedEvent.from(stocks));

    return new RegisterResponse(command.productId());
  }

  private List<Stock> convertToStocks(StockInitializeCommand request) {
    return request.productVariants().stream()
        .map(variant -> new InitialStock(request.productId(), variant.id(), variant.quantity()))
        .map(Stock::create)
        .toList();
  }

  private void validateUniqueVariants(StockInitializeCommand command) {
    long distinctCount =
        command.productVariants().stream()
            .map(StockInitializeCommand.ProductVariant::id)
            .distinct()
            .count();

    if (distinctCount != command.productVariants().size()) {
      throw new StockApplicationException(ApplicationErrorCode.DUPLICATED_VARIANT);
    }
  }
}
