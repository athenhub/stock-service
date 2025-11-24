package com.athenhub.stockservice.product.domain;

import com.athenhub.stockservice.global.domain.AbstractTimeEntity;
import com.athenhub.stockservice.product.domain.vo.ProductId;
import com.athenhub.stockservice.product.domain.vo.ProductVariantId;
import com.athenhub.stockservice.product.domain.vo.StockId;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 재고(Stock)를 나타내는 도메인 엔티티.
 *
 * <p>특정 상품(Product)과 옵션(ProductVariant)에 대한 현재 재고 수량을 관리한다.
 *
 * <p>재고의 모든 변경은 {@link #increase(int)}, {@link #decrease(int)} 메서드를 통해서만 이루어진다.
 *
 * <p>{@link Version}을 통해 낙관적 락(Optimistic Lock)을 적용하여 동시 수정 상황에서도 데이터 정합성을 보장한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Entity
@Table(name = "p_stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stock extends AbstractTimeEntity {

  /** 재고 식별자. */
  @EmbeddedId private StockId id;

  /** 상품 식별자. */
  @Embedded private ProductId productId;

  /** 상품 옵션 식별자. */
  @Embedded private ProductVariantId variantId;

  /** 현재 재고 수량. */
  private int quantity;

  /** 낙관적 락 처리를 위한 버전 값. */
  @Version private Long version;

  /**
   * Stock 생성자.
   *
   * @param quantity 초기 재고 수량 (1 이상)
   * @param productId 상품 ID
   * @param variantId 상품 옵션 ID
   */
  private Stock(int quantity, ProductId productId, ProductVariantId variantId) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("재고 수량은 1 이상입니다.");
    }

    this.quantity = quantity;
    this.id = StockId.create();
    this.productId = Objects.requireNonNull(productId);
    this.variantId = Objects.requireNonNull(variantId);
  }

  /** 재고 엔티티 생성을 위한 정적 팩토리 메서드. */
  public static Stock of(int quantity, ProductId productId, ProductVariantId variantId) {
    return new Stock(quantity, productId, variantId);
  }

  /**
   * 재고를 증가시킨다.
   *
   * @param amount 증가할 수량 (1 이상)
   */
  public void increase(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("증가 수량은 1 이상이어야 합니다.");
    }
    this.quantity += amount;
  }

  /**
   * 재고를 감소시킨다.
   *
   * @param amount 감소할 수량 (1 이상)
   * @throws IllegalStateException 재고가 부족한 경우
   */
  public void decrease(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("감소 수량은 1 이상이어야 합니다.");
    }

    if (this.quantity < amount) {
      throw new IllegalStateException("재고가 부족합니다.");
    }

    this.quantity -= amount;
  }
}
