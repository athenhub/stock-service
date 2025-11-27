package com.athenhub.stockservice.stock.application.dto;

import java.util.List;
import java.util.UUID;

/**
 * 상품 재고 초기화를 위한 커맨드 객체이다.
 *
 * <p>특정 상품(Product)에 대해 여러 개의 옵션(Variant)별 초기 재고 수량을 전달하기 위한 애플리케이션 계층의 입력 모델이다.
 *
 * @param productId 재고를 초기화할 상품 ID
 * @param productVariants 상품의 옵션(Variant) 목록
 * @author 김지원
 * @since 1.0.0
 */
public record StockInitializeCommand(

    // 재고를 초기화할 상품 ID.
    UUID productId,

    // 상품의 옵션(Variant)별 초기 재고 정보 목록.
    List<ProductVariant> productVariants) {

  /**
   * 상품의 개별 옵션(Variant)에 대한 초기 재고 정보를 나타낸다.
   *
   * @param id 상품 옵션(Variant) ID
   * @param quantity 초기 재고 수량
   * @author 김지원
   * @since 1.0.0
   */
  public record ProductVariant(

      // 상품 옵션(Variant) ID.
      UUID id,

      // 해당 옵션의 초기 재고 수량. */
      int quantity) {}
}
