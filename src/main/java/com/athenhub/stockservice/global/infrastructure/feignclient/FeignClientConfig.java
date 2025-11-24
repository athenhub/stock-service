package com.athenhub.stockservice.global.infrastructure.feignclient;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign Client 설정 클래스.
 *
 * <p>현재 요청(HttpServletRequest)에 포함된 인증/사용자 정보를 Feign 요청 헤더에 그대로 전달하기 위한 설정을 담당한다.
 *
 * <p>이를 통해 마이크로서비스 간 호출에서도 인증 및 사용자 컨텍스트가 유지된다.
 */
@Configuration
@EnableFeignClients("com.athenhub.stockservice")
@RequiredArgsConstructor
public class FeignClientConfig {

  /** 사용자 ID 전달용 헤더 명. */
  private static final String HEADER_USER_ID = "X-User-Id";

  /** 사용자 로그인 아이디(Header) 전달용 변수. */
  private static final String HEADER_USERNAME = "X-Username";

  /** 사용자 역할 정보 전달용 헤더 명. */
  private static final String HEADER_ROLES = "X-User-Roles";

  /** 사용자 이메일 전달용 헤더 명. */
  private static final String HEADER_EMAIL = "X-User-Email";

  /** 사용자 이름 전달용 헤더 명. */
  private static final String HEADER_USER_NAME = "X-User-Name";

  /**
   * Feign 요청 시 현재 HTTP 요청의 헤더 정보를 함께 전달하기 위한 인터셉터.
   *
   * <p>현재 요청에 JWT 토큰(Authorization 헤더)가 존재하면 해당 값을 그대로 전달한다.
   *
   * <p>또한, 서비스 간 인증을 위해 다음 헤더를 함께 전달한다.
   *
   * <ul>
   *   <li>X-User-Id
   *   <li>X-Username
   *   <li>X-User-Name
   *   <li>X-User-Email
   *   <li>X-User-Roles
   * </ul>
   *
   * <p>이 설정을 통해 Feign으로 호출되는 다른 서비스에서도 원 요청자의 인증/권한 정보를 그대로 활용할 수 있다.
   *
   * @return RequestInterceptor Feign 요청 인터셉터
   */
  @Bean
  public RequestInterceptor requestInterceptor() {

    return template -> {
      // 현재 실행 중인 요청의 Attributes 가져오기
      RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

      if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // JWT 전달 (user-service 인증 용)
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)) {
          template.header("Authorization", authorization);
        }

        // 그 외 마이크로서비스용 사용자 정보 헤더 전달
        String userId = request.getHeader(HEADER_USER_ID);
        String username = request.getHeader(HEADER_USERNAME);
        String name = request.getHeader(HEADER_USER_NAME);
        String email = request.getHeader(HEADER_EMAIL);
        String roles = request.getHeader(HEADER_ROLES);

        if (StringUtils.hasText(userId)) {
          template.header(HEADER_USER_ID, userId);
        }
        if (StringUtils.hasText(username)) {
          template.header(HEADER_USERNAME, username);
        }
        if (StringUtils.hasText(name)) {
          template.header(HEADER_USER_NAME, name);
        }
        if (StringUtils.hasText(email)) {
          template.header(HEADER_EMAIL, email);
        }
        if (StringUtils.hasText(roles)) {
          template.header(HEADER_ROLES, roles);
        }
      }
    };
  }
}
