package com.athenhub.stockservice.stock.domain.service;

import com.athenhub.stockservice.stock.domain.dto.ProductVariantMatchCommand;

/**
 * 상품 옵션(Variant) 일관성 검증을 담당하는 도메인 서비스이다.
 *
 * <p>재고 등록 요청에 포함된 옵션 목록과 실제 상품에 등록된 옵션 목록이 일치하는지 검증하는 역할을 한다.
 *
 * <p>이를 통해 존재하지 않는 옵션에 대한 재고 등록이나 불일치한 옵션 정보가 저장되는 것을 방지한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface ProductVariantConsistencyValidator {

  /**
   * 상품 옵션 목록이 실제 상품과 일치하는지 검증한다.
   *
   * @param existenceCommand 상품 및 옵션 일관성 검증 요청 정보
   * @return 옵션 목록이 일치하면 {@code true}, 일치하지 않으면 {@code false}
   * @author 김지원
   * @since 1.0.0
   */
  boolean matches(ProductVariantMatchCommand existenceCommand);
}
