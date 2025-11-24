package com.athenhub.stockservice.stock.infrastructure.client.product;

import com.athenhub.stockservice.global.infrastructure.feignclient.FeignClientConfig;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 외부 Product 서비스와 통신하기 위한 Feign Client이다.
 *
 * <p>상품 정보를 조회하여 재고 서비스의 권한 검증 및 비즈니스 로직에서 활용한다. (예: 상품이 요청자의 허브/업체에 속하는지 확인)
 *
 * <p>기본 경로는 {@code /api/v1/products} 이며, 단건 상품 조회 API를 제공한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequestMapping("/api/v1/products")
@FeignClient(name = "product-service", configuration = FeignClientConfig.class)
public interface ProductClient {

  /**
   * 상품 ID로 상품 상세 정보를 조회한다.
   *
   * @param productId 조회할 상품의 식별자
   * @return 상품 상세 정보
   */
  @GetMapping("/{productId}")
  ProductDetail getProduct(@PathVariable UUID productId);
}
