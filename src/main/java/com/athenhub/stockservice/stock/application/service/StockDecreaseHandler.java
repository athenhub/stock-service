package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseRequest;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.event.external.StockDecreaseFailedEvent;
import com.athenhub.stockservice.stock.domain.event.external.StockDecreaseSuccessEvent;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.vo.OrderId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 주문 단위로 재고를 감소시키는 서비스이다.
 *
 * <p>동시에 동일한 재고를 갱신하려는 요청이 발생할 경우 Optimistic Lock 충돌이 발생할 수 있으며, 이 경우 자동 재시도를 통해 처리를 보완한다.
 *
 * <p>이미 처리된 주문에 대해서는 이력 조회를 통해 멱등성이 보장된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class StockDecreaseHandler {

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;
  private final StockDecreaseSuccessEventPublisher stockDecreasedEventPublisher;
  private final StockDecreaseFailedEventPublisher stockDecreaseFailedEventPublisher;

  /**
   * 주문 단위로 재고를 감소시킨다.
   *
   * <p>Optimistic Lock 기반으로 충돌이 발생할 수 있으며, 해당 예외 발생 시 일정 횟수만큼 자동 재시도를 수행한다.
   *
   * <p>동일한 주문에 대한 중복 처리는 {@link StockHistoryRepository#existsByOrderId(OrderId)}를 통해 차단된다.
   *
   * @param orderId 재고 감소를 수행할 주문 ID
   * @param requests 재고 감소 요청 목록
   * @author 김지원
   * @since 1.0.0
   */
  @Retryable(
      value = {ObjectOptimisticLockingFailureException.class, OptimisticLockException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional
  public void decreaseAll(UUID orderId, @Valid List<StockDecreaseRequest> requests) {

    OrderId order = OrderId.of(orderId);

    // 멱등성 보장
    if (stockHistoryRepository.existsByOrderId(order)) {
      return;
    }

    List<StockHistory> histories =
        requests.stream().map(request -> decreaseSingleStock(order, request)).toList();

    saveHistories(histories);

    publishEvent(orderId);
  }

  @Recover
  public void recover(
      ObjectOptimisticLockingFailureException e,
      UUID orderId,
      List<StockDecreaseRequest> requests) {
    log.error("StockDecreaseFailed(orderId={})", orderId, e);
    stockDecreaseFailedEventPublisher.publish(
        StockDecreaseFailedEvent.of(orderId, "OPTIMISTIC_LOCK_CONFLICT"));
  }

  @Recover
  public void recover(
      OptimisticLockException e, UUID orderId, List<StockDecreaseRequest> requests) {
    log.error("StockDecreaseFailed(orderId={})", orderId, e);
    stockDecreaseFailedEventPublisher.publish(
        StockDecreaseFailedEvent.of(orderId, "OPTIMISTIC_LOCK_CONFLICT"));
  }

  /**
   * 단일 재고를 감소시키고 이력을 생성한다.
   *
   * @param orderId 주문 ID
   * @param request 재고 감소 요청
   * @return 생성된 재고 이력
   * @author 김지원
   * @since 1.0.0
   */
  private StockHistory decreaseSingleStock(OrderId orderId, StockDecreaseRequest request) {

    Stock stock =
        stockRepository.findByVariantId(ProductVariantId.of(request.variantId())).orElseThrow();

    stock.decrease(request.quantity());

    return StockHistory.outbound(stock, orderId, request.quantity());
  }

  /**
   * 재고 이력을 저장한다.
   *
   * @param histories 저장할 재고 이력 목록
   * @author 김지원
   * @since 1.0.0
   */
  private void saveHistories(List<StockHistory> histories) {
    stockHistoryRepository.saveAll(histories);
  }

  /**
   * 재고 감소 완료 이벤트를 발행한다.
   *
   * @param orderId 주문 ID
   * @author 김지원
   * @since 1.0.0
   */
  private void publishEvent(UUID orderId) {
    stockDecreasedEventPublisher.publish(StockDecreaseSuccessEvent.of(orderId));
  }
}
