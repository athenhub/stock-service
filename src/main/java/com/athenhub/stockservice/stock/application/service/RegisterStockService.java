package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import com.athenhub.stockservice.stock.application.exception.ApplicationErrorCode;
import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.dto.InitialStock;
import com.athenhub.stockservice.stock.domain.event.external.StockRegisteredEvent;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterStockService {

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;
  private final StockRegisteredEventPublisher stockRegisteredEventPublisher;

  public RegisterResponse register(StockInitializeCommand command) {
    validateUniqueVariants(command);

    List<Stock> stocks = saveStocks(command);

    saveStockHistory(stocks);

    publishEvent(command.productId());
    return new RegisterResponse(command.productId());
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

  private List<Stock> saveStocks(StockInitializeCommand command) {
    List<Stock> stocks = convertToStocks(command);
    stockRepository.saveAll(stocks);
    return stocks;
  }

  private List<Stock> convertToStocks(StockInitializeCommand request) {
    return request.productVariants().stream()
        .map(variant -> new InitialStock(request.productId(), variant.id(), variant.quantity()))
        .map(Stock::create)
        .toList();
  }

  private void saveStockHistory(List<Stock> stocks) {
    List<StockHistory> stockHistories =
        stocks.stream().map(stock -> StockHistory.inbound(stock, stock.getQuantity())).toList();
    stockHistoryRepository.saveAll(stockHistories);
  }

  private void publishEvent(UUID productId) {
    stockRegisteredEventPublisher.publish(StockRegisteredEvent.from(productId));
  }
}
