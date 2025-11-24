package com.athenhub.stockservice.stock.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 재고 도메인에서 사용되는 사용자 접근 정보를 나타내는 DTO이다.
 *
 * <p>재고 생성 및 변경 시, 요청한 사용자가 어떤 허브(hub) 또는 업체(vendor)에 속해 있는지 판단하기 위한 최소한의 컨텍스트 정보를 전달한다.
 *
 * <p>주요 사용 목적:
 *
 * <ul>
 *   <li>소속 검증 (BelongsToValidator)
 *   <li>상품 접근 권한 검증 (ProductAccessPermissionValidator)
 *   <li>Audit, History, Event 발행 시 컨텍스트 전달
 * </ul>
 *
 * @param memberId 사용자 고유 식별자 (필수)
 * @param hubId 사용자가 속한 허브 식별자 (nullable)
 * @param vendorId 사용자가 속한 업체 식별자 (nullable)
 * @author 김지원
 * @since 1.0.0
 */
public record AccessContext(@NotNull UUID memberId, UUID hubId, UUID vendorId) {}
