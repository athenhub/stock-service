package com.athenhub.stockservice.stock.infrastructure.client.member.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Member 서비스로부터 전달받는 사용자 정보 DTO이다.
 *
 * <p>주로 재고 서비스에서 사용자 소속(허브/업체) 및 권한 판단을 위해 사용된다. 외부 시스템의 응답 구조를 그대로 매핑하는 목적의 객체이며, 도메인 로직은 포함하지
 * 않는다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record MemberInfo(
    UUID id,
    String name,
    String username,
    String slackId,
    UUID organizationId,
    OrganizationType organizationType,
    String organizationName,
    MemberRole role,
    MemberStatus status,
    LocalDateTime deletedAt,
    String deletedBy) {}
