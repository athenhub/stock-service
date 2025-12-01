package com.athenhub.stockservice.stock.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * 재고 등록 요청을 위한 DTO이다.
 *
 * <p>클라이언트로부터 전달받은 상품과 옵션(Variant)별 초기 재고 수량 정보를 캡슐화한다.
 *
 * @param hubId 재고를 등록할 허브 ID
 * @param vendorId 재고를 등록할 벤더 ID
 * @param productId 재고를 등록할 상품 ID
 * @param productVariants 상품 옵션(Variant) 목록과 초기 재고 수량
 * @author 김지원
 * @since 1.0.0
 */
public record RegisterStockRequest(

    /* 재고를 등록할 허브 ID. */
    UUID hubId,

    /* 재고를 등록할 벤더 ID. */
    UUID vendorId,

    /* 재고를 등록할 상품 ID. */
    @NotNull UUID productId,

    /** 상품 옵션(Variant) 및 초기 재고 수량 목록. */
    @NotNull List<ProductVariant> productVariants) {

  /**
   * 상품의 개별 옵션과 초기 재고 수량을 나타낸다.
   *
   * @param id 상품 옵션(Variant) ID
   * @param quantity 초기 재고 수량 (1 이상)
   * @author 김지원
   * @since 1.0.0
   */
  public record ProductVariant(

      /* 상품 옵션(Variant) ID. */
      @NotNull UUID id,

      /* 초기 재고 수량 (1 이상). */
      @Min(1) int quantity) {}
}
