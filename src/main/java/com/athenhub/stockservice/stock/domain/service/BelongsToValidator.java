package com.athenhub.stockservice.stock.domain.service;

import com.athenhub.stockservice.stock.domain.dto.AccessContext;

/**
 * 사용자가 특정 허브(Hub) 또는 업체(Vendor)에 소속되어 있는지 검증하는 도메인 서비스이다.
 *
 * <p>재고 등록, 수정, 삭제 등의 행위가 발생할 때 요청자가 해당 재고에 대한 소속 권한을 가지고 있는지 판단하기 위해 사용된다.
 *
 * <p>구현체는 인프라 계층(예: Membership API, 사내 사용자 서비스 등)에 위치하며, 도메인 계층은 이 인터페이스에만 의존한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface BelongsToValidator {

  /**
   * 사용자가 주어진 컨텍스트의 허브/업체에 소속되어 있는지 확인한다.
   *
   * @param context 사용자, 허브, 업체 정보를 포함한 접근 컨텍스트
   * @return 소속되어 있는 경우 true, 그렇지 않으면 false
   */
  boolean belongsTo(AccessContext context);
}
