package com.athenhub.stockservice.stock.application.listener;

import com.athenhub.stockservice.stock.domain.StockEventType;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.event.internal.StockDecreasedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 재고 관련 도메인 이벤트를 수신하여 재고 이력(StockHistory)을 저장하는 이벤트 리스너이다.
 *
 * <p>Stock 도메인에서 발생하는 생성/차감 이벤트를 감지하여 {@link StockHistory}를 생성하고 영속화한다.
 *
 * <p>이벤트 기반 처리를 통해 Stock 도메인과 이력 도메인의 결합도를 최소화하고 책임을 분리한다.
 *
 * <p>각 이벤트는 {@link Transactional} 환경에서 처리되어 이력 저장이 원자적으로 보장된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class StockHistoryEventListener {

  /** 재고 이력 저장을 위한 레포지토리. */
  private final StockHistoryRepository stockHistoryRepository;

  /**
   * {@link StockCreatedEvent} 발생 시 호출되어 재고 생성 이력을 저장한다.
   *
   * <p>이벤트 타입은 {@link StockEventType#INBOUND}로 지정되며, 수량은 {@link StockEventType#signed(int)} 를 통해
   * 양수로 변환된다.
   *
   * @param event 생성된 재고 정보를 담고 있는 도메인 이벤트
   */
  @Transactional
  @EventListener
  public void onStockCreated(StockCreatedEvent event) {
    List<StockHistory> histories = convertToHistories(event);
    stockHistoryRepository.saveAll(histories);
  }

  /**
   * {@link StockDecreasedEvent} 발생 시 호출되어 재고 차감 이력을 저장한다.
   *
   * <p>이벤트 타입은 {@link StockEventType#OUTBOUND}로 지정되며, 수량은 {@link StockEventType#signed(int)} 를 통해
   * 음수로 변환된다.
   *
   * @param event 차감된 재고 정보를 담고 있는 도메인 이벤트
   */
  @Transactional
  @EventListener
  public void onStockDecreased(StockDecreasedEvent event) {
    StockEventType outbound = StockEventType.OUTBOUND;

    StockHistory history =
        StockHistory.create(
            outbound.signed(event.quantity()),
            event.stockId(),
            event.productId(),
            event.variantId(),
            outbound);

    stockHistoryRepository.save(history);
  }

  private List<StockHistory> convertToHistories(StockCreatedEvent event) {
    StockEventType inbound = StockEventType.INBOUND;

    return event.stocks().stream()
        .map(
            it ->
                StockHistory.create(
                    inbound.signed(it.quantity()),
                    it.stockId(),
                    it.productId(),
                    it.variantId(),
                    inbound))
        .toList();
  }
}
