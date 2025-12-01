package com.athenhub.stockservice.stock.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 여러 상품의 재고 감소 요청을 하나의 배치 이벤트로 전달하기 위한 DTO이다.
 *
 * <p>주문 서비스에서 발행한 주문 생성 이벤트 또는 내부 처리 단계에서 재고 서비스가 한 번에 여러 상품의 재고를 감소시키도록 전달할 때 사용된다.
 *
 * <p>이 이벤트는 다음 내용을 포함한다:
 *
 * <ul>
 *   <li>orderId — 재고 감소 요청이 속한 주문 식별자
 *   <li>orderedAt — 주문이 생성된 시점
 *   <li>stockDecreaseRequests — 감소해야 할 상품별 재고 감소 요청 목록
 * </ul>
 *
 * <p>재고 서비스의 StockDecreaseHandler가 본 이벤트를 기반으로 재고 감소 처리, 히스토리 생성, 이벤트 발행 등을 수행한다.
 *
 * @param orderId 재고 감소 요청이 속한 주문 ID
 * @param orderedAt 주문 생성 시각
 * @param stockDecreaseRequests 재고 감소 요청 목록
 * @author 김지원
 * @since 1.0.0
 */
public record StockDecreaseBatchEvent(
    UUID orderId, LocalDateTime orderedAt, List<StockDecreaseRequest> stockDecreaseRequests) {}
