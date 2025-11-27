package com.athenhub.stockservice.stock.domain;

/**
 * 재고 변동 유형을 나타내는 열거형이다.
 *
 * <p>재고 변경이 발생한 원인을 의미하며, 이력(StockHistory) 또는 이벤트 처리 시 사용된다.
 *
 * <ul>
 *   <li>{@code INBOUND} : 입고 (+)
 *   <li>{@code OUTBOUND} : 출고 (-)
 *   <li>{@code CANCEL} : 주문 취소 (+)
 *   <li>{@code RETURN} : 반품 (+)
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum StockEventType {

  /**
   * 입고.
   *
   * <p>해당 수량을 그대로 유지한다(+).
   */
  INBOUND {
    @Override
    public int signed(int quantity) {
      return quantity;
    }
  },

  /**
   * 출고.
   *
   * <p>해당 수량에 음수(-) 부호를 적용한다.
   */
  OUTBOUND {
    @Override
    public int signed(int quantity) {
      return quantity * -1;
    }
  },

  /**
   * 주문 취소.
   *
   * <p>차감된 재고를 복구하는 개념이므로 양수(+)로 반환한다.
   */
  CANCEL {
    @Override
    public int signed(int quantity) {
      return quantity;
    }
  },

  /**
   * 반품.
   *
   * <p>재고가 다시 들어오는 개념이므로 양수(+)로 반환한다.
   */
  RETURN {
    @Override
    public int signed(int quantity) {
      return quantity;
    }
  };

  /**
   * 입고(INBOUND) 여부를 반환한다.
   *
   * @return 입고이면 {@code true}
   */
  public boolean isInbound() {
    return this == INBOUND;
  }

  /**
   * 출고(OUTBOUND) 여부를 반환한다.
   *
   * @return 출고이면 {@code true}
   */
  public boolean isOutbound() {
    return this == OUTBOUND;
  }

  /**
   * 주문 취소(CANCEL) 여부를 반환한다.
   *
   * @return 주문 취소이면 {@code true}
   */
  public boolean isCancel() {
    return this == CANCEL;
  }

  /**
   * 반품(RETURN) 여부를 반환한다.
   *
   * @return 반품이면 {@code true}
   */
  public boolean isReturn() {
    return this == RETURN;
  }

  /**
   * 이벤트 타입에 따라 수량에 부호(+/-)를 부여한다.
   *
   * <p>양수는 재고 증가, 음수는 재고 감소를 의미한다.
   *
   * @param quantity 원본 수량 (절대값 기준)
   * @return 부호가 적용된 수량
   */
  public abstract int signed(int quantity);
}
