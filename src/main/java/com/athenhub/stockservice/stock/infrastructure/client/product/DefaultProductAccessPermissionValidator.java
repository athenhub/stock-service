package com.athenhub.stockservice.stock.infrastructure.client.product;

import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionValidator;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 상품에 대한 접근 권한을 검증하는 기본 구현체이다.
 *
 * <p>외부 Product 서비스를 조회하여, 해당 상품이 요청자의 컨텍스트(허브/업체)와 동일한 소속인지 여부를 판단한다.
 *
 * <p>ProductClient를 통해 상품 정보를 조회하고, 상품의 hubId 또는 vendorId가 AccessContext의 값과 일치하는 경우 접근 가능으로 판단한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DefaultProductAccessPermissionValidator implements ProductAccessPermissionValidator {

  private final ProductClient productClient;

  /**
   * 사용자가 특정 상품에 접근할 수 있는지 여부를 판단한다.
   *
   * @param accessContext 접근 주체의 컨텍스트(회원, 허브, 업체 정보)
   * @param productId 접근하려는 상품 ID
   * @return 접근 가능하면 true, 아니면 false
   */
  @Override
  public boolean canAccess(AccessContext accessContext, UUID productId) {
    ProductDetail product = productClient.getProduct(productId);
    return isProductInSameContext(accessContext, product);
  }

  /**
   * 상품이 요청자의 허브 또는 업체에 속하는지 확인한다.
   *
   * @param accessContext 접근 주체의 컨텍스트
   * @param product 상품 상세 정보
   * @return 동일한 허브 또는 업체에 속하면 true, 아니면 false
   */
  private static boolean isProductInSameContext(
      AccessContext accessContext, ProductDetail product) {
    return Objects.equals(accessContext.hubId(), product.hubId())
        || Objects.equals(accessContext.vendorId(), product.vendorId());
  }
}
