package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseRequest;
import com.athenhub.stockservice.stock.application.event.external.StockDecreaseSuccessEvent;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.vo.OrderId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class StockDecreaseHandler {

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;
  private final StockDecreaseSuccessEventPublisher decreaseSuccessEventPublisher;

  @Transactional
  public void decreaseAll(UUID orderId, @Valid List<StockDecreaseRequest> requests) {

    OrderId order = OrderId.of(orderId);

    if (stockHistoryRepository.existsByOrderId(order)) {
      return; // 멱등성 보장
    }

    List<StockHistory> histories =
        requests.stream().map(request -> decreaseSingleStock(order, request)).toList();

    stockHistoryRepository.saveAll(histories);

    decreaseSuccessEventPublisher.publish(StockDecreaseSuccessEvent.of(orderId));
  }

  private StockHistory decreaseSingleStock(OrderId orderId, StockDecreaseRequest request) {

    Stock stock =
        stockRepository.findByVariantId(ProductVariantId.of(request.variantId())).orElseThrow();

    stock.decrease(request.quantity());

    return StockHistory.outbound(stock, orderId, request.quantity());
  }
}
