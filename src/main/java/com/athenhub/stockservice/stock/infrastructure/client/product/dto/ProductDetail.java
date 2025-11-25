package com.athenhub.stockservice.stock.infrastructure.client.product.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 상품 서비스로부터 전달받는 최소 상품 정보 DTO이다.
 *
 * <p>재고 서비스에서는 상품 전체 정보가 아닌, <b>소속 정보</b>(Hub / Vendor)에 대해서만 관심이 있으므로 해당 필드만 포함한다.
 *
 * <ul>
 *   <li>{@code hubId} : 상품이 소속된 Hub의 식별자
 *   <li>{@code vendorId} : 상품이 소속된 Vendor(업체)의 식별자
 * </ul>
 *
 * <p>주요 사용 목적:
 *
 * <ul>
 *   <li>재고 등록 시 접근 권한 검증
 *   <li>요청 사용자의 조직 정보와 비교
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductDetail(
    UUID productId,
    String name,
    String description,
    Long price,
    UUID hubId,
    UUID vendorId,
    ProductType type,
    ProductStatus status,
    List<ProductVariantDetails> variants,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt) {}
