package com.athenhub.stockservice.stock.domain;

/**
 * 재고 변동 유형을 나타내는 열거형이다.
 *
 * <p>재고 변경이 발생한 원인을 의미하며, 이력(StockHistory) 또는 이벤트 처리 시 사용된다.
 *
 * <ul>
 *   <li>{@code INBOUND} : 입고
 *   <li>{@code OUTBOUND} : 출고
 *   <li>{@code CANCEL} : 주문 취소
 *   <li>{@code RETURN} : 반품
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum StockEventType {

  /** 입고. */
  INBOUND,

  /** 출고. */
  OUTBOUND,

  /** 주문 취소. */
  CANCEL;

  public boolean isInbound() {
    return this == INBOUND;
  }

  public boolean isOutbound() {
    return this == OUTBOUND;
  }

  public boolean isCancel() {
    return this == CANCEL;
  }
}
