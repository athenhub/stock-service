package com.athenhub.stockservice.stock.infrastructure.client.member;

/**
 * 회원의 상태(Status)를 나타내는 열거형이다.
 *
 * <p>외부 Member 서비스에서 관리하는 계정의 현재 활성 상태를 표현하며, 재고 서비스에서는 접근 가능 여부, 정책 적용 시 참고용으로 활용될 수 있다.
 *
 * <ul>
 *   <li>{@code PENDING} : 승인 대기 상태
 *   <li>{@code REJECTED} : 승인 거절 상태
 *   <li>{@code ACTIVATED} : 활성 상태
 *   <li>{@code DEACTIVATED} : 비활성 상태
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum MemberStatus {

  /** 승인 대기 상태. */
  PENDING,

  /** 승인 거절 상태. */
  REJECTED,

  /** 활성 상태. */
  ACTIVATED,

  /** 비활성 상태. */
  DEACTIVATED
}
