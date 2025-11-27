package com.athenhub.stockservice.stock.infrastructure.client.product.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Product 서비스 연동을 위한 Feign 설정 클래스이다.
 *
 * <p>Product Feign Client 호출 중 발생하는 예외를 도메인/애플리케이션 예외로 변환하기 위한 {@link ErrorDecoder}를 등록한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
public class ProductFeignConfig {

  /**
   * Product Feign Client 전용 ErrorDecoder를 등록한다.
   *
   * <p>Product 서비스로부터 전달되는 HTTP 상태 코드 또는 오류 응답을 {@link ProductFeignErrorDecoder}를 통해 적절한 예외로 변환한다.
   *
   * @return ProductFeignErrorDecoder
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public ErrorDecoder productFeignErrorDecoder() {
    return new ProductFeignErrorDecoder();
  }
}
