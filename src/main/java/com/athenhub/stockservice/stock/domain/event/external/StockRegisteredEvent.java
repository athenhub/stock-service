package com.athenhub.stockservice.stock.domain.event.external;

import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재고(Stock)가 등록되었음을 외부 시스템에 알리기 위한 이벤트이다.
 *
 * <p>내부 도메인 이벤트인 {@link StockCreatedEvent}를 외부 전파 목적에 맞게 단순화한 이벤트 객체이며, 주로 RabbitMQ, Kafka 등 메시지
 * 브로커를 통해 다른 서비스(예: Product 서비스, Analytics 서비스 등)로 전달된다.
 *
 * <p>내부 이벤트와 분리하여 외부에 필요한 정보만 전달함으로써 도메인 구조 노출을 최소화하고 결합도를 낮춘다.
 *
 * @param stockId 생성된 재고(Stock)의 고유 식별자
 * @param productId 재고가 속한 상품(Product) 식별자
 * @param productVariantId 재고가 속한 상품 옵션(Variant) 식별자
 * @param quantity 등록된 재고 수량
 * @author 김지원
 * @since 1.0.0
 */
public record StockRegisteredEvent(
    UUID stockId, UUID productId, UUID productVariantId, int quantity, LocalDateTime registeredAt) {

  /**
   * 내부 도메인 이벤트({@link StockCreatedEvent})를 외부 전파용 이벤트로 변환한다.
   *
   * <p>내부 식별자(Value Object)를 외부에서 사용 가능한 {@link UUID} 형태로 변환하여 메시징 시스템을 통해 다른 서비스로 전달할 수 있도록 한다.
   *
   * @param event 내부 재고 생성 이벤트
   * @return 외부 전파용 재고 등록 이벤트
   */
  public static StockRegisteredEvent from(StockCreatedEvent event) {
    return new StockRegisteredEvent(
        event.stockId().toUuid(),
        event.productId().toUuid(),
        event.variantId().toUuid(),
        event.quantity(),
        event.createdAt());
  }
}
