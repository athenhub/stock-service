package com.athenhub.stockservice.stock.application.service;

import static com.athenhub.stockservice.stock.application.exception.ApplicationErrorCode.STOCK_DECREASE_CONFLICT;

import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.dto.StockDecreaseRequest;
import com.athenhub.stockservice.stock.domain.event.internal.StockDecreasedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.vo.OrderId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 주문 단위로 재고를 감소시키는 서비스이다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Validated
@Service
@RequiredArgsConstructor
public class StockDecreaseHandler {

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;
  private final StockDecreasedEventPublisher stockDecreasedEventPublisher;

  /** 주문 단위로 재고를 감소시킨다. */
  @Transactional
  public void decreaseAll(UUID orderId, @Valid List<StockDecreaseRequest> requests) {

    OrderId order = OrderId.of(orderId);

    // 멱등성 보장
    if (stockHistoryRepository.existsByOrderId(order)) {
      return;
    }

    try {
      List<StockHistory> histories =
          requests.stream().map(request -> decreaseSingleStock(order, request)).toList();

      saveHistories(histories);

      publishEvent(orderId);

    } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
      throw new StockApplicationException(STOCK_DECREASE_CONFLICT);
    }
  }

  /** 단일 재고를 감소시키고, 이력을 생성한다. */
  private StockHistory decreaseSingleStock(OrderId orderId, StockDecreaseRequest request) {

    Stock stock =
        stockRepository.findByVariantId(ProductVariantId.of(request.variantId())).orElseThrow();

    stock.decrease(request.quantity());

    return StockHistory.outbound(stock, orderId, request.quantity());
  }

  /** 재고 이력을 저장한다. */
  private void saveHistories(List<StockHistory> histories) {
    stockHistoryRepository.saveAll(histories);
  }

  /** 재고 감소 완료 이벤트를 발행한다. */
  private void publishEvent(UUID orderId) {
    stockDecreasedEventPublisher.publish(StockDecreasedEvent.of(orderId));
  }
}
