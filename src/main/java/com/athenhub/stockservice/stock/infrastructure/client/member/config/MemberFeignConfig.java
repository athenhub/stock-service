package com.athenhub.stockservice.stock.infrastructure.client.member.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Member 서비스와의 연동을 위한 Feign 설정 클래스.
 *
 * <p>Member Feign Client에서 발생하는 예외를 도메인/애플리케이션 예외로 변환하기 위한 {@link ErrorDecoder}를 등록한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
public class MemberFeignConfig {

  /**
   * Member Feign Client 전용 ErrorDecoder를 등록한다.
   *
   * <p>Member 서비스에서 전달되는 HTTP 상태 코드 기반 응답을 {@link MemberFeignErrorDecoder}를 통해 적절한 예외로 변환한다.
   *
   * @return MemberFeignErrorDecoder
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public ErrorDecoder memberFeignErrorDecoder() {
    return new MemberFeignErrorDecoder();
  }
}
