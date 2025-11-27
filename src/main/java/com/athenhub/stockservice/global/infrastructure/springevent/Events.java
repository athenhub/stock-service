package com.athenhub.stockservice.global.infrastructure.springevent;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Spring ApplicationEvent 발행을 위한 정적 유틸리티 클래스.
 *
 * <p>도메인 및 애플리케이션 계층이 Spring Framework 의존 없이 이벤트를 발행할 수 있도록 중간 브리지 역할을 한다.
 *
 * <p>실제 {@link ApplicationEventPublisher}는 애플리케이션 시작 시 외부(Spring Bean)에서 설정되며, 이후 {@link
 * #trigger(Object)}를 통해 어디에서든 이벤트를 발행할 수 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public class Events {

  /**
   * Spring ApplicationEventPublisher 인스턴스.
   *
   * <p>애플리케이션 시작 시 {@link #setPublisher(ApplicationEventPublisher)}를 통해 주입된다.
   */
  private static ApplicationEventPublisher publisher;

  /**
   * ApplicationEventPublisher를 등록한다.
   *
   * <p>보통 Spring Bean(PostConstruct)에서 한 번만 호출되며, 이 설정 이후 {@link #trigger(Object)}를 통해 이벤트 발행이
   * 가능해진다.
   *
   * @param publisher Spring ApplicationEventPublisher
   */
  static void setPublisher(ApplicationEventPublisher publisher) {
    Events.publisher = publisher;
  }

  /**
   * 주어진 이벤트를 Spring ApplicationEvent로 발행한다.
   *
   * <p>publisher가 아직 설정되지 않은 경우(null)에는 아무 작업도 수행하지 않고 조용히 종료된다.
   *
   * @param event 발행할 이벤트 객체
   */
  public static void trigger(Object event) {
    if (publisher == null) {
      return;
    }
    publisher.publishEvent(event);
  }
}
