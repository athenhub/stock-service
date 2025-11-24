package com.athenhub.stockservice.stock.domain.service;

import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import java.util.UUID;

/**
 * 사용자가 특정 상품(Product)에 접근할 권한이 있는지 검증하는 도메인 서비스이다.
 *
 * <p>재고 등록, 수정, 삭제와 같이 상품과 직접적으로 연관된 작업을 수행하기 전에 해당 사용자가 해당 상품에 대해 접근(조회/수정) 가능한지 판단하기 위해 사용된다.
 *
 * <p>구현체는 외부 시스템(예: Product Service, Authorization Service 등)을 호출하는 인프라 계층에 위치하며, 도메인 계층은 본 인터페이스에만
 * 의존한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface ProductAccessPermissionValidator {

  /**
   * 주어진 상품에 대해 사용자가 접근할 수 있는지 여부를 판단한다.
   *
   * @param accessContext 사용자, 허브, 업체 정보를 포함한 접근 컨텍스트
   * @param productId 접근 대상 상품의 식별자
   * @return 접근 가능하면 true, 그렇지 않으면 false
   */
  boolean canAccess(AccessContext accessContext, UUID productId);
}
