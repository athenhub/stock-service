package com.athenhub.stockservice.product.domain;

import com.athenhub.stockservice.global.domain.AbstractTimeEntity;
import com.athenhub.stockservice.product.domain.vo.ProductId;
import com.athenhub.stockservice.product.domain.vo.ProductVariantId;
import com.athenhub.stockservice.product.domain.vo.StockHistoryId;
import com.athenhub.stockservice.product.domain.vo.StockId;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
@Table(name = "p_stock_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StockHistory extends AbstractTimeEntity {

  /** 재고 이력 식별자. */
  @EmbeddedId private StockHistoryId id;

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
   * @param changedQuantity 변경된 재고 수량 (0 불가)
   * @param stockId 재고 식별자
   * @param productId 상품 식별자
   * @param variantId 상품 옵션 식별자
   * @param eventType 재고 변동 이벤트 유형
   */
  public StockHistory(
      int changedQuantity,
      StockId stockId,
      ProductId productId,
      ProductVariantId variantId,
      StockEventType eventType) {
    if (changedQuantity == 0) {
      throw new IllegalArgumentException("변경하려는 재고 수량은 0이 될 수 없습니다.");
    }

    Objects.requireNonNull(eventType, "EventType 은 null 이 되어서는 안됩니다.");

    // 이벤트 타입에 따른 수량 부호 제한
    if (eventType == StockEventType.INBOUND
        || eventType == StockEventType.RETURN
        || eventType == StockEventType.CANCEL) {
      if (changedQuantity < 0) {
        throw new IllegalArgumentException("입고/반품/취소는 양수 수량만 가능합니다.");
      }
    }

    if (eventType == StockEventType.OUTBOUND) {
      if (changedQuantity > 0) {
        throw new IllegalArgumentException("출고는 음수 수량만 가능합니다.");
      }
    }

    this.id = StockHistoryId.create();
    this.stockId = Objects.requireNonNull(stockId);
    this.productId = Objects.requireNonNull(productId);
    this.variantId = Objects.requireNonNull(variantId);
    this.eventType = eventType;
    this.changedQuantity = changedQuantity;
  }

  /** 재고 이력 생성을 위한 정적 팩토리 메서드. */
  public static StockHistory of(
      int changedQuantity,
      StockId stockId,
      ProductId productId,
      ProductVariantId variantId,
      StockEventType eventType) {
    return new StockHistory(changedQuantity, stockId, productId, variantId, eventType);
  }
}
