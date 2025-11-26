package com.athenhub.stockservice.stock.domain.dto;

import java.util.UUID;

/**
 * 초기 재고(Initial Stock) 생성 정보를 표현하는 DTO이다.
 *
 * <p>재고 등록 시 상품과 옵션(Variant)에 대해 최초 재고 수량을 전달하기 위한 용도로 사용된다.
 *
 * @param productId 재고를 등록할 상품 ID
 * @param variantId 재고를 등록할 상품 옵션(Variant) ID
 * @param quantity 초기 재고 수량
 * @author 김지원
 * @since 1.0.0
 */
public record InitialStock(

    /** 재고를 등록할 상품 ID. */
    UUID productId,

    /** 재고를 등록할 상품 옵션(Variant) ID. */
    UUID variantId,

    /** 초기 재고 수량. */
    int quantity) {}
