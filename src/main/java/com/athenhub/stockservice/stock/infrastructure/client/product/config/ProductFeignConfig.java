package com.athenhub.stockservice.stock.infrastructure.client.product.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductFeignConfig {

  @Bean
  public ErrorDecoder productFeignErrorDecoder() {
    return new ProductFeignErrorDecoder();
  }
}
