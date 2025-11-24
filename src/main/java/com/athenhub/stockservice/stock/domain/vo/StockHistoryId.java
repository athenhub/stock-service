package com.athenhub.stockservice.stock.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 재고 이력(StockHistory)의 식별자를 나타내는 값 객체이다.
 *
 * <p>UUID 기반 식별자이며 불변(Immutable) 값 객체로 사용된다. JPA에서 다른 엔티티에 내장(Embedded)되어 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class StockHistoryId {

  /** 재고 이력 ID 값. */
  private UUID id;

  /** 내부 UUID 값을 반환한다. */
  public UUID toUuid() {
    return id;
  }

  /** UUID를 기반으로 StockHistoryId를 생성한다. */
  private StockHistoryId(UUID id) {
    this.id = Objects.requireNonNull(id);
  }

  /** 기존 UUID를 감싸 StockHistoryId를 생성한다. */
  public static StockHistoryId of(UUID uuid) {
    return new StockHistoryId(Objects.requireNonNull(uuid));
  }

  /** 새로운 UUID를 생성하여 StockHistoryId를 생성한다. */
  public static StockHistoryId create() {
    return new StockHistoryId(UUID.randomUUID());
  }

  /** UUID 값을 문자열로 반환한다. */
  @Override
  public String toString() {
    return id.toString();
  }
}
