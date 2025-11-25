package com.athenhub.stockservice.global.infrastructure.feignclient;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign Client 설정 클래스이다.
 *
 * <p>현재 요청(HttpServletRequest)에 포함된 인증 및 사용자 정보를 Feign 요청 헤더에 그대로 전달하기 위한 역할을 수행한다.
 *
 * <p>이를 통해 마이크로서비스 간 호출에서도 원 요청자의 인증 / 권한 / 사용자 컨텍스트를 유지할 수 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
@EnableFeignClients("com.athenhub.stockservice")
@RequiredArgsConstructor
public class FeignClientConfig {

  /** 사용자 ID 전달용 헤더 명. */
  private static final String HEADER_USER_ID = "X-User-Id";

  /** 사용자 로그인 아이디 전달용 헤더 명. */
  private static final String HEADER_USERNAME = "X-Username";

  /** 사용자 역할 정보 전달용 헤더 명. */
  private static final String HEADER_ROLES = "X-User-Roles";

  /** 사용자 slackId 전달용 헤더 명. */
  private static final String HEADER_SLACK_ID = "X-Slack-Id";

  /** 사용자 이름 전달용 헤더 명. */
  private static final String HEADER_USER_NAME = "X-User-Name";

  /** 인증 토큰(JWT) 전달용 헤더 명. */
  private static final String HEADER_AUTHORIZATION = "Authorization";

  /**
   * Feign 요청 시 현재 HTTP 요청의 헤더 정보를 함께 전달하기 위한 인터셉터이다.
   *
   * <p>현재 요청에 포함된 다음 헤더를 Feign 요청에 그대로 복사한다.
   *
   * <ul>
   *   <li>Authorization (JWT)
   *   <li>X-User-Id
   *   <li>X-Username
   *   <li>X-User-Name
   *   <li>X-User-Email
   *   <li>X-User-Roles
   * </ul>
   *
   * <p>이를 통해 다른 서비스에서도 현재 사용자의 컨텍스트 정보를 동일하게 사용할 수 있도록 한다.
   *
   * @return RequestInterceptor Feign 요청 인터셉터
   */
  @Bean
  public RequestInterceptor requestInterceptor() {

    return template -> {
      // 현재 실행 중인 HTTP 요청 정보 조회
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

      if (attributes == null) {
        return;
      }

      HttpServletRequest request = attributes.getRequest();

      // Authorization 전달
      copyToHeader(template, request, HEADER_AUTHORIZATION);

      // 사용자 관련 헤더 전달
      copyToHeader(template, request, HEADER_USER_ID);
      copyToHeader(template, request, HEADER_USERNAME);
      copyToHeader(template, request, HEADER_USER_NAME);
      copyToHeader(template, request, HEADER_SLACK_ID);
      copyToHeader(template, request, HEADER_ROLES);
    };
  }

  /**
   * 특정 헤더를 현재 요청에서 읽어 Feign 요청에 복사한다.
   *
   * <p>헤더 값이 비어있지 않은 경우에만 전달한다.
   *
   * @param template Feign 요청 템플릿
   * @param request 현재 HTTP 요청
   * @param headerName 전달할 헤더 이름
   */
  private void copyToHeader(
      RequestTemplate template, HttpServletRequest request, String headerName) {

    String value = request.getHeader(headerName);

    if (StringUtils.hasText(value)) {
      template.header(headerName, value);
    }
  }
}
