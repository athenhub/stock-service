package com.athenhub.stockservice.stock.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 재고(Stock) 등록을 위한 요청 DTO이다.
 *
 * <p>어떤 상품(Product)과 옵션(Variant)에 대해 몇 개의 재고를 등록할 것인지 전달하는 객체이다.
 *
 * <p>애플리케이션 및 컨트롤러 계층에서 전달되며, 도메인 내부에서는 {@link com.athenhub.stockservice.stock.domain.Stock} 생성 시
 * 사용된다.
 *
 * @param productId 재고를 등록할 상품의 고유 식별자
 * @param variantId 재고를 등록할 상품 옵션의 고유 식별자
 * @param quantity 등록할 재고 수량 (최소 1 이상)
 * @author 김지원
 * @since 1.0.0
 */
public record RegisterRequest(
    @NotNull UUID productId, @NotNull UUID variantId, @Min(1) int quantity) {}
