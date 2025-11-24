package com.athenhub.stockservice.stock.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 상품 옵션(ProductVariant)의 식별자를 나타내는 값 객체이다.
 *
 * <p>UUID 기반 식별자이며, 엔티티가 아닌 값 객체(Value Object)로 사용된다. JPA에서 다른 엔티티에 내장(Embedded)되어 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ProductVariantId {

  /** 상품 옵션 ID 값. */
  @Column(name = "product_variant_id")
  private UUID id;

  /** 내부 UUID 값을 반환한다. */
  public UUID toUuid() {
    return id;
  }

  /** UUID를 기반으로 ProductVariantId를 생성한다. */
  private ProductVariantId(UUID id) {
    this.id = Objects.requireNonNull(id);
  }

  /** 기존 UUID를 감싸 ProductVariantId를 생성한다. */
  public static ProductVariantId of(UUID uuid) {
    return new ProductVariantId(Objects.requireNonNull(uuid));
  }

  /** UUID 값을 문자열로 반환한다. */
  @Override
  public String toString() {
    return id.toString();
  }
}
