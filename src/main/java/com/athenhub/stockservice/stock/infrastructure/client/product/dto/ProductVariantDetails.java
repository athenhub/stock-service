package com.athenhub.stockservice.stock.infrastructure.client.product.dto;

import java.util.UUID;

/**
 * Product 서비스로부터 조회한 상품 옵션(Variant) 상세 정보를 나타내는 DTO이다.
 *
 * <p>옵션의 색상, 사이즈, 재고 수량 등 재고 서비스에서 검증 또는 비교용으로 사용하는 최소한의 정보를 담는다.
 *
 * @param variantId 상품 옵션(Variant) ID
 * @param color 상품 색상
 * @param size 상품 사이즈
 * @param quantity 해당 옵션의 재고 수량
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantDetails(

    /** 상품 옵션(Variant) ID. */
    UUID variantId,

    /** 상품 색상. */
    String color,

    /** 상품 사이즈. */
    String size,

    /** 해당 옵션의 재고 수량. */
    int quantity) {}
