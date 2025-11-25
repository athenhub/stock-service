package com.athenhub.stockservice.stock.infrastructure.client.member.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberFeignConfig {

  @Bean
  public ErrorDecoder memberFeignErrorDecoder() {
    return new MemberFeignErrorDecoder();
  }
}
