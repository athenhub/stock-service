package com.athenhub.stockservice.stock.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재고 감소 요청 정보를 전달하는 DTO이다.
 *
 * <p>주문 생성 등 외부 이벤트에 의해 특정 상품 옵션(Variant)의 재고를 감소시켜야 할 때 사용된다.
 *
 * @param productId 재고를 감소시킬 상품 ID
 * @param variantId 재고를 감소시킬 상품 옵션(Variant) ID
 * @param quantity 감소시킬 수량 (1 이상)
 * @param requestAt 요청 발생 시각
 * @author 김지원
 * @since 1.0.0
 */
public record StockDecreaseRequest(

    /** 재고를 감소시킬 상품 ID. */
    @NotNull UUID productId,

    /** 재고를 감소시킬 상품 옵션(Variant) ID. */
    @NotNull UUID variantId,

    /** 감소시킬 재고 수량 (1 이상). */
    @Min(1) int quantity,

    /** 재고 감소 요청이 발생한 시각. */
    @NotNull LocalDateTime requestAt) {}
