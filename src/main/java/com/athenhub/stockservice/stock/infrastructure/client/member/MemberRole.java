package com.athenhub.stockservice.stock.infrastructure.client.member;

/**
 * 회원의 역할(Role)을 나타내는 열거형이다.
 *
 * <p>외부 Member 서비스에서 내려주는 권한 정보를 표현하며, 재고 서비스에서는 주로 접근 권한 및 소속 검증 로직에서 활용된다.
 *
 * <ul>
 *   <li>{@code MASTER_MANAGER} : 전체 시스템에 대한 최고 관리자
 *   <li>{@code HUB_MANAGER} : 특정 허브(Hub)의 관리자
 *   <li>{@code SHIPPING_AGENT} : 배송 담당자
 *   <li>{@code VENDOR_AGENT} : 특정 업체(Vendor)의 담당자
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum MemberRole {

  /** 전체 시스템에 대한 최고 관리자. */
  MASTER_MANAGER,

  /** 특정 허브(Hub)의 관리자. */
  HUB_MANAGER,

  /** 배송을 담당하는 사용자. */
  SHIPPING_AGENT,

  /** 특정 업체(Vendor)의 담당자. */
  VENDOR_AGENT
}
