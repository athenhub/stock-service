package com.athenhub.stockservice.stock.application.listener;

import com.athenhub.stockservice.stock.domain.StockEventType;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * StockCreatedEvent를 수신하여 재고 이력(StockHistory)을 저장하는 이벤트 리스너이다.
 *
 * <p>재고(Stock)가 생성되면 내부적으로 {@link StockCreatedEvent}가 발행되며, 이 리스너는 해당 이벤트를 감지하여 재고 이력 정보를 생성하고
 * 저장한다.
 *
 * <p>이 설계를 통해 Stock 도메인과 StockHistory 도메인의 결합도를 낮추고 이벤트 기반으로 책임을 분리할 수 있다.
 *
 * <p>{@link Transactional}이 적용되어 있으므로 이벤트 처리 과정에서 하나의 트랜잭션으로 이력 저장이 보장된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class StockHistoryEventListener {

  /** 재고 이력을 저장하기 위한 레포지토리 */
  private final StockHistoryRepository stockHistoryRepository;

  /**
   * StockCreatedEvent 발생 시 호출되어 StockHistory를 생성하고 저장한다.
   *
   * @param event 생성된 재고 정보를 담고 있는 도메인 이벤트
   */
  @Transactional
  @EventListener
  public void onStockCreated(StockCreatedEvent event) {
    StockHistory history =
        StockHistory.create(
            event.quantity(),
            event.stockId(),
            event.productId(),
            event.variantId(),
            StockEventType.INBOUND);

    stockHistoryRepository.save(history);
  }
}
