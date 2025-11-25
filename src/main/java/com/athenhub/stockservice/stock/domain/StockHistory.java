package com.athenhub.stockservice.stock.domain;

import com.athenhub.stockservice.global.domain.AbstractTimeEntity;
import com.athenhub.stockservice.stock.domain.vo.OrderId;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import com.athenhub.stockservice.stock.domain.vo.StockHistoryId;
import com.athenhub.stockservice.stock.domain.vo.StockId;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 재고 변동 이력(StockHistory)을 나타내는 도메인 엔티티.
 *
 * <p>상품(Product)과 옵션(ProductVariant)의 재고 변경 내역을 기록한다. 변경된 수량과 발생 원인을 나타내는 이벤트 유형을 함께 저장한다.
 *
 * <p>이 엔티티는 "현재 재고"가 아닌, "변경 이력"만을 담당한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Entity
@Table(
    name = "p_stock_history",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_order_variant",
          columnNames = {"order_id", "product_variant_id"})
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StockHistory extends AbstractTimeEntity {

  /** 재고 이력 식별자. */
  @EmbeddedId private StockHistoryId id;

  /** 주문 이력 식별자. */
  @Embedded private OrderId orderId;

  /** 재고 식별자. */
  @Embedded private StockId stockId;

  /** 상품 식별자. */
  @Embedded private ProductId productId;

  /** 상품 옵션 식별자. */
  @Embedded private ProductVariantId variantId;

  /** 재고 변동 이벤트 유형. */
  @Enumerated(EnumType.STRING)
  private StockEventType eventType;

  /** 변경된 재고 수량. */
  private int changedQuantity;

  /**
   * StockHistory 생성자.
   *
   * <p>수량과 이벤트 타입에 대한 도메인 규칙을 검증한 후 재고 이력을 생성한다.
   *
   * @param changedQuantity 변경된 재고 수량 (0 불가)
   * @param stockId 재고 식별자
   * @param productId 상품 식별자
   * @param variantId 상품 옵션 식별자
   * @param eventType 재고 변동 이벤트 유형
   */
  private StockHistory(
      int changedQuantity,
      StockId stockId,
      OrderId orderId,
      ProductId productId,
      ProductVariantId variantId,
      StockEventType eventType) {

    validateEventTypeNotNull(eventType);
    validateQuantityNotZero(changedQuantity);
    validateQuantitySign(changedQuantity, eventType);

    this.id = StockHistoryId.create();
    this.orderId = orderId; // 재고 등록시에는 주문 번호가 없다.(nullable)
    this.stockId = Objects.requireNonNull(stockId);
    this.productId = Objects.requireNonNull(productId);
    this.variantId = Objects.requireNonNull(variantId);
    this.eventType = Objects.requireNonNull(eventType);
    this.changedQuantity = changedQuantity;
  }

  public static StockHistory of(
      int changedQuantity,
      StockId stockId,
      ProductId productId,
      ProductVariantId variantId,
      StockEventType eventType) {
    return new StockHistory(changedQuantity, stockId, null, productId, variantId, eventType);
  }

  public static StockHistory inbound(Stock stock, int quantity) {
    StockEventType inbound = StockEventType.INBOUND;
    return new StockHistory(
        inbound.signed(quantity),
        stock.getId(),
        null,
        stock.getProductId(),
        stock.getVariantId(),
        inbound);
  }

  public static StockHistory outbound(Stock stock, OrderId orderId, int quantity) {
    StockEventType type = StockEventType.OUTBOUND;
    return new StockHistory(
        type.signed(quantity),
        stock.getId(),
        orderId,
        stock.getProductId(),
        stock.getVariantId(),
        type);
  }

  /**
   * 이벤트 타입 null 여부를 검증한다.
   *
   * <p>eventType은 반드시 존재해야 하며, null일 경우 예외를 발생시킨다.
   *
   * @param eventType 재고 변동 이벤트 유형
   */
  private void validateEventTypeNotNull(StockEventType eventType) {
    Objects.requireNonNull(eventType, "EventType 은 null 이 되어서는 안됩니다.");
  }

  /**
   * 변경 수량이 0이 아닌지 검증한다.
   *
   * <p>재고 변경은 반드시 증가 또는 감소가 있어야 하므로 0은 허용되지 않는다.
   *
   * @param changedQuantity 변경된 재고 수량
   */
  private void validateQuantityNotZero(int changedQuantity) {
    if (changedQuantity == 0) {
      throw new IllegalArgumentException("변경하려는 재고 수량은 0이 될 수 없습니다.");
    }
  }

  /**
   * 이벤트 유형에 따른 수량 부호의 유효성을 검증한다.
   *
   * <p>재고 변동 유형(입고/출고/취소)에 따라 변경 수량의 부호(양수/음수)가 올바른지 검증한다.
   *
   * <ul>
   *   <li>입고(INBOUND) : 양수만 허용
   *   <li>출고(OUTBOUND) : 음수만 허용
   *   <li>취소(CANCEL) : 양수만 허용
   * </ul>
   *
   * @param changedQuantity 변경된 재고 수량
   * @param eventType 재고 변동 이벤트 유형
   */
  private void validateQuantitySign(int changedQuantity, StockEventType eventType) {
    validateInboundQuantitySign(eventType, changedQuantity);
    validateCancelQuantitySign(eventType, changedQuantity);
    validateOutboundQuantitySign(eventType, changedQuantity);
  }

  /**
   * 출고(OUTBOUND) 이벤트의 수량 부호를 검증한다.
   *
   * <p>출고는 재고 감소를 의미하므로 음수 수량만 허용된다.
   *
   * @param eventType 재고 변동 이벤트 유형
   * @param changedQuantity 변경된 재고 수량
   */
  private void validateOutboundQuantitySign(StockEventType eventType, int changedQuantity) {
    if (eventType.isOutbound() && changedQuantity > 0) {
      throw new IllegalArgumentException("출고는 음수 수량만 가능합니다.");
    }
  }

  /**
   * 주문 취소(CANCEL) 이벤트의 수량 부호를 검증한다.
   *
   * <p>주문 취소는 재고 증가를 의미하므로 양수 수량만 허용된다.
   *
   * @param eventType 재고 변동 이벤트 유형
   * @param changedQuantity 변경된 재고 수량
   */
  private void validateCancelQuantitySign(StockEventType eventType, int changedQuantity) {
    if (eventType.isCancel() && changedQuantity < 0) {
      throw new IllegalArgumentException("주문 취소는 양수 수량만 가능합니다.");
    }
  }

  /**
   * 입고(INBOUND) 이벤트의 수량 부호를 검증한다.
   *
   * <p>입고는 재고 증가를 의미하므로 양수 수량만 허용된다.
   *
   * @param eventType 재고 변동 이벤트 유형
   * @param changedQuantity 변경된 재고 수량
   */
  private void validateInboundQuantitySign(StockEventType eventType, int changedQuantity) {
    if (eventType.isInbound() && changedQuantity < 0) {
      throw new IllegalArgumentException("입고는 양수 수량만 가능합니다.");
    }
  }
}
