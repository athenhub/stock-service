package com.athenhub.stockservice.global.infrastructure.springevent;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Events 유틸 클래스에 ApplicationEventPublisher를 바인딩하기 위한 설정 클래스이다.
 *
 * <p>Spring 컨텍스트가 초기화된 이후, {@link ApplicationContext}를 {@link
 * Events#setPublisher(ApplicationEventPublisher)}에 주입하여 도메인 / 애플리케이션 계층에서 정적 메서드로 Spring Event를 발행할
 * 수 있도록 한다.
 *
 * <p>또한 {@link EnableAsync}를 통해 비동기 이벤트 리스너 구성을 허용한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@EnableAsync
@Configuration
public class EventConfig {

  /**
   * Spring ApplicationContext.
   *
   * <p>ApplicationEventPublisher 역할을 함께 수행하며, Events 클래스의 publisher로 등록된다.
   */
  @Autowired private ApplicationContext ctx;

  /**
   * Spring 컨텍스트 초기화 이후 실행되는 InitializingBean.
   *
   * <p>이 시점에서 {@link Events#setPublisher(ApplicationEventPublisher)}를 호출하여 Events 유틸에
   * ApplicationEventPublisher를 바인딩한다.
   *
   * <p>이를 통해 도메인 영역에서는 Spring에 직접 의존하지 않고도 {@code Events.trigger(...)} 방식으로 안전하게 이벤트를 발행할 수 있다.
   *
   * @return ApplicationContext 를 Events에 바인딩하는 InitializingBean
   */
  @Bean
  public InitializingBean eventsInitializer() {
    return () -> Events.setPublisher(ctx);
  }
}
