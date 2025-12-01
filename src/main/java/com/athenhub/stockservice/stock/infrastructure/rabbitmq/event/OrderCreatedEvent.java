package com.athenhub.stockservice.stock.infrastructure.rabbitmq.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 주문 생성 완료를 알리는 이벤트이다.
 *
 * <p>Order 서비스에서 주문이 생성된 이후 발행되며, 재고 서비스가 이를 수신하여 상품 옵션(Variant)별 재고 감소 처리를 수행하기 위해 사용된다.
 *
 * @param orderId 생성된 주문 ID
 * @param products 주문에 포함된 상품 목록
 * @param orderedAt 주문이 생성된 시각
 * @author 김지원
 * @since 1.0.0
 */
public record OrderCreatedEvent(

    /* 생성된 주문 ID. */
    UUID orderId,

    /* 주문에 포함된 상품 및 옵션 목록. */
    List<OrderedProduct> products,

    /* 주문이 생성된 시각. */
    LocalDateTime orderedAt) {

  /**
   * 주문에 포함된 개별 상품 정보를 나타낸다.
   *
   * <p>재고 감소 처리를 위해 상품 ID, 옵션 ID, 수량 정보를 포함한다.
   *
   * @param productId 주문된 상품 ID
   * @param variantId 주문된 상품 옵션(Variant) ID
   * @param quantity 주문 수량
   * @author 김지원
   * @since 1.0.0
   */
  public record OrderedProduct(

      /* 주문된 상품 ID. */
      UUID productId,

      /* 주문된 상품 옵션(Variant) ID. */
      UUID variantId,

      /* 주문 수량. */
      int quantity) {}
}
