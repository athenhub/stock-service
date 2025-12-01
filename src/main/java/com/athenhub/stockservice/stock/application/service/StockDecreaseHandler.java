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

/**
 * 재고 감소 요청을 처리하고 성공 이벤트를 발행하는 핵심 도메인 서비스이다.
 *
 * <p>본 서비스는 다음의 책임을 갖는다:
 *
 * <ul>
 *   <li>여러 재고 감소 요청을 묶어서 처리하는 배치 처리
 *   <li>각 상품별 재고 감소 및 Stock 엔티티 상태 검증
 *   <li>재고 감소 이력을 StockHistory로 생성 및 저장
 *   <li>중복 주문에 대한 멱등성(Idempotency) 보장
 *   <li>재고 감소 성공 시 Success 이벤트 발행
 * </ul>
 *
 * <p>특히 멱등성을 보장하기 위해 동일한 주문 ID에 대해 이미 처리된 기록이 존재한다면 재고 감소 로직을 다시 수행하지 않는다.
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
  private final StockDecreaseSuccessEventPublisher decreaseSuccessEventPublisher;

  /**
   * 여러 상품의 재고 감소 요청을 하나의 주문 단위로 처리한다.
   *
   * <p>주요 처리 흐름은 다음과 같다:
   *
   * <ol>
   *   <li>OrderId 변환
   *   <li>이미 동일 주문 ID로 처리된 기록이 있는지 확인하여 멱등성 보장
   *   <li>각 요청별로 재고 감소 수행
   *   <li>재고 감소 이력 저장
   *   <li>재고 감소 성공 이벤트 발행
   * </ol>
   *
   * @param orderId 주문 ID(UUID)
   * @param requests 재고 감소 요청 목록
   */
  @Transactional
  public void decreaseAll(UUID orderId, @Valid List<StockDecreaseRequest> requests) {

    OrderId order = OrderId.of(orderId);

    // 멱등성 보장: 이미 동일 주문 ID로 감소가 처리되었다면 아무 작업도 수행하지 않음
    if (stockHistoryRepository.existsByOrderId(order)) {
      return;
    }

    // 각 요청별 재고 감소 처리 후 StockHistory 생성
    List<StockHistory> histories =
        requests.stream().map(request -> decreaseSingleStock(order, request)).toList();

    // 재고 감소 이력 저장
    stockHistoryRepository.saveAll(histories);

    // 성공 이벤트 발행 (주문 서비스 등 외부 도메인으로 전달)
    decreaseSuccessEventPublisher.publish(StockDecreaseSuccessEvent.of(orderId));
  }

  /**
   * 단일 상품의 재고를 감소시키고 해당 작업에 대한 이력을 생성한다.
   *
   * <p>처리 절차:
   *
   * <ul>
   *   <li>상품별 재고 엔티티 조회
   *   <li>재고 감소 수행 (수량 검증 포함)
   *   <li>StockHistory 생성
   * </ul>
   *
   * @param orderId 주문 ID
   * @param request 단일 상품 재고 감소 요청
   * @return 생성된 재고 감소 이력 엔티티
   */
  private StockHistory decreaseSingleStock(OrderId orderId, StockDecreaseRequest request) {

    // 재고 엔티티 조회
    Stock stock =
        stockRepository.findByVariantId(ProductVariantId.of(request.variantId())).orElseThrow();

    // 재고 감소 수행 (도메인 규칙에 따라 수량 검증 포함)
    stock.decrease(request.quantity());

    // 재고 감소 이력 생성
    return StockHistory.outbound(stock, orderId, request.quantity());
  }
}
