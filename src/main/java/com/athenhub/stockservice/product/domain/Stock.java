package com.athenhub.stockservice.product.domain;

import com.athenhub.stockservice.global.domain.AbstractTimeEntity;
import com.athenhub.stockservice.product.domain.dto.AccessContext;
import com.athenhub.stockservice.product.domain.dto.RegisterRequest;
import com.athenhub.stockservice.product.domain.exception.StockDomainException;
import com.athenhub.stockservice.product.domain.service.BelongsToValidator;
import com.athenhub.stockservice.product.domain.service.ProductAccessPermissionChecker;
import com.athenhub.stockservice.product.domain.vo.ProductId;
import com.athenhub.stockservice.product.domain.vo.ProductVariantId;
import com.athenhub.stockservice.product.domain.vo.StockId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.athenhub.stockservice.product.domain.exception.PermissionErrorCode.REGISTER_NOT_ALLOWED;

/**
 * 상품 재고(Stock)를 나타내는 도메인 엔티티.
 *
 * <p>특정 상품(Product)과 옵션(ProductVariant)에 대한 현재 재고 수량을 관리한다.
 *
 * <p>재고의 모든 변경은 {@link #increase(int)}, {@link #decrease(int)} 메서드를 통해서만 이루어지며, 외부에서 직접 set 할 수 없다.
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

  /** 재고 엔티티의 식별자. */
  @EmbeddedId private StockId id;

  /** 재고가 속한 상품의 식별자. */
  @Embedded private ProductId productId;

  /** 재고가 속한 상품 옵션의 식별자. */
  @Embedded private ProductVariantId variantId;

  /** 현재 재고 수량. */
  private int quantity;

  /** 낙관적 락을 위한 버전 값. */
  @Version private Long version;

  /**
   * Stock 생성자.
   *
   * <p>초기 재고 수량을 검증한 후 상품 / 옵션 식별자와 재고 수량을 설정한다.
   *
   * @param quantity 초기 재고 수량 (1 이상)
   * @param productId 상품 ID
   * @param variantId 상품 옵션 ID
   */
  private Stock(int quantity, ProductId productId, ProductVariantId variantId) {
    validateInitialQuantity(quantity);

    this.id = StockId.create();
    this.productId = Objects.requireNonNull(productId, "productId는 null이 될 수 없습니다.");
    this.variantId = Objects.requireNonNull(variantId, "variantId는 null이 될 수 없습니다.");
    this.quantity = quantity;
  }

  /**
   * Stock 엔티티 생성을 위한 정적 팩토리 메서드.
   *
   * @return 생성된 Stock 객체
   */
  public static Stock of(
          RegisterRequest request,
          AccessContext context,
          BelongsToValidator belongsToValidator,
          ProductAccessPermissionChecker permissionChecker
  ) {
      if (!belongsToValidator.belongsTo(context)) {
          throw new StockDomainException(REGISTER_NOT_ALLOWED, "현재 사용자는 해당 허브/벤더에 소속되어 있지 않습니다.");
      }
      if (!permissionChecker.canAccess(context, request.productId())) {
          throw new StockDomainException(REGISTER_NOT_ALLOWED, "해당 상품에 대한 재고 등록 권한이 없습니다.");
      }

      return new Stock(
              request.quantity(),
              ProductId.of(Objects.requireNonNull(request.productId())),
              ProductVariantId.of(Objects.requireNonNull(request.variantId())));
  }

  /**
   * 재고를 증가시킨다.
   *
   * <p>증가 수량을 검증한 후 재고에 반영한다.
   *
   * @param amount 증가할 수량 (1 이상)
   */
  public void increase(int amount) {
    validateIncreaseAmount(amount);
    applyIncrease(amount);
  }

  /**
   * 재고를 감소시킨다.
   *
   * <p>감소 수량과 현재 재고를 검증한 후 재고를 차감한다.
   *
   * @param amount 감소할 수량 (1 이상)
   * @throws IllegalStateException 재고가 부족한 경우
   */
  public void decrease(int amount) {
    validateDecreaseAmount(amount);
    validateStockAvailability(amount);
    applyDecrease(amount);
  }

  /**
   * 초기 재고 수량의 유효성을 검증한다.
   *
   * <p>초기 재고는 반드시 1 이상이어야 한다.
   *
   * @param quantity 초기 재고 수량
   */
  private void validateInitialQuantity(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("재고 수량은 1 이상입니다.");
    }
  }

  /**
   * 증가 수량의 유효성을 검증한다.
   *
   * <p>증가 수량은 반드시 1 이상이어야 한다.
   *
   * @param amount 증가할 수량
   */
  private void validateIncreaseAmount(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("증가 수량은 1 이상이어야 합니다.");
    }
  }

  /**
   * 재고 수량을 실제로 증가시킨다.
   *
   * @param amount 증가할 수량
   */
  private void applyIncrease(int amount) {
    this.quantity += amount;
  }

  /**
   * 감소 수량의 유효성을 검증한다.
   *
   * <p>감소 수량은 반드시 1 이상이어야 한다.
   *
   * @param amount 감소할 수량
   */
  private void validateDecreaseAmount(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("감소 수량은 1 이상이어야 합니다.");
    }
  }

  /**
   * 현재 재고가 충분한지 검증한다.
   *
   * <p>현재 재고가 감소 요청 수량보다 적으면 예외를 발생시킨다.
   *
   * @param amount 감소할 수량
   */
  private void validateStockAvailability(int amount) {
    if (this.quantity < amount) {
      throw new IllegalStateException("재고가 부족합니다.");
    }
  }

  /**
   * 재고 수량을 실제로 감소시킨다.
   *
   * @param amount 감소할 수량
   */
  private void applyDecrease(int amount) {
    this.quantity -= amount;
  }
}
