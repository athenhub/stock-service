package com.athenhub.stockservice.stock.infrastructure.client.member;

import com.athenhub.stockservice.stock.infrastructure.client.config.FeignClientConfig;
import com.athenhub.stockservice.stock.infrastructure.client.member.config.MemberFeignConfig;
import com.athenhub.stockservice.stock.infrastructure.client.member.dto.MemberInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 회원(Member) 정보를 조회하기 위한 Feign Client이다.
 *
 * <p>외부 Member 서비스의 {@code /api/v1/members/me} API를 호출하여 현재 로그인한 사용자의 조직(허브/업체) 및 권한 정보를 조회한다.
 *
 * <p>주요 사용 목적:
 *
 * <ul>
 *   <li>{@link com.athenhub.stockservice.stock.domain.service.BelongsToValidator} 의 구현체에서 소속 검증에 사용
 *   <li>사용자 기반 접근 제어(Authorization)
 * </ul>
 *
 * <p>FeignClient 설정은 {@link FeignClientConfig}를 통해 공통 설정을 적용한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@FeignClient(
    name = "member-service",
    path = "/api/v1/members",
    configuration = {FeignClientConfig.class, MemberFeignConfig.class})
public interface MemberClient {

  /**
   * 현재 로그인한 사용자의 정보를 조회한다.
   *
   * @return 사용자 및 소속 정보를 담은 MemberInfo
   */
  @GetMapping("/me")
  MemberInfo getMyInfo();
}
