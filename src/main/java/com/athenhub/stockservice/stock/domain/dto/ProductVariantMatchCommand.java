package com.athenhub.stockservice.stock.domain.dto;

import java.util.List;
import java.util.UUID;

/**
 * 상품의 옵션(Variant) 목록 일치 여부를 검증하기 위한 커맨드 객체이다.
 *
 * <p>상품에 실제로 등록된 옵션 목록과 재고 등록 요청에 포함된 옵션 목록이 일치하는지 비교할 때 사용된다.
 *
 * @param productId 검증 대상 상품 ID
 * @param productVariantIds 검증할 상품 옵션(Variant) ID 목록
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantMatchCommand(

    /* 검증 대상 상품 ID. */
    UUID productId,

    /* 비교 대상 상품 옵션(Variant) ID 목록. */
    List<UUID> productVariantIds) {}
