package com.athenhub.stockservice.stock.infrastructure.client.member;

/**
 * 회원이 속한 조직 유형을 나타내는 열거형이다.
 *
 * <p>사용자가 어느 조직(허브 또는 벤더)에 소속되어 있는지를 표현하며, 권한 검증 및 소속 검증 로직에서 활용된다.
 *
 * <ul>
 *   <li>{@code NONE} : 소속 없음
 *   <li>{@code HUB} : 허브 소속
 *   <li>{@code VENDOR} : 업체(벤더) 소속
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum OrganizationType {

  /** 소속 없음. */
  NONE,

  /** 허브 소속. */
  HUB,

  /** 업체(벤더) 소속. */
  VENDOR;

  /**
   * 조직 유형이 벤더(VENDOR)인지 여부를 반환한다.
   *
   * @return 벤더 소속이면 true, 아니면 false
   */
  public boolean isVendor() {
    return this == VENDOR;
  }

  /**
   * 조직 유형이 허브(HUB)인지 여부를 반환한다.
   *
   * @return 허브 소속이면 true, 아니면 false
   */
  public boolean isHub() {
    return this == HUB;
  }
}
