package com.athenhub.stockservice.stock.infrastructure.rabbitmq.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 메시지 직렬화/역직렬화를 위한 설정 클래스이다.
 *
 * <p>RabbitMQ와 메시지를 주고받는 과정에서 이벤트 객체를 JSON 형태로 변환하기 위해 Jackson 기반의 메시지 컨버터를 등록한다. 이를 통해 재고 감소 이벤트,
 * 주문 이벤트 등 모든 메시지 간에 일관된 직렬화 포맷(JSON)을 보장한다.
 *
 * <p>또한 Java 8 날짜/시간(LocalDateTime 등)을 정상적으로 변환하기 위해 {@link JavaTimeModule} 을 등록한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
public class MessageConverterConfig {

  /**
   * RabbitMQ에 전달되는 메시지를 JSON으로 직렬화/역직렬화하는 컨버터를 등록한다.
   *
   * <p>메시지 Publisher는 내부 이벤트 객체를 JSON으로 변환하여 큐에 발행하고, 메시지 Listener는 JSON 메시지를 Java 객체로 역직렬화할 때 본
   * 컨버터가 사용된다.
   *
   * <p>내부적으로 {@link ObjectMapper} 를 직접 설정하여 다음과 같은 요구사항을 충족한다:
   *
   * <ul>
   *   <li>Java Time(LocalDateTime 등) 직렬화 지원
   *   <li>알 수 없는 속성 무시 (서비스 간 확장성, 하위 호환성 보장)
   * </ul>
   *
   * @return JSON 직렬화를 위한 {@link Jackson2JsonMessageConverter}
   */
  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter(rabbitObjectMapper());
  }

  /**
   * RabbitMQ 메시지 직렬화에 특화된 {@link ObjectMapper}를 생성한다.
   *
   * <p>다음과 같은 설정을 기본 적용한다:
   *
   * <ul>
   *   <li>{@link JavaTimeModule} 등록 → LocalDateTime 정상 직렬화
   *   <li>{@code FAIL_ON_UNKNOWN_PROPERTIES = false} → 스키마 확장 시 하위 호환성 유지
   * </ul>
   *
   * @return 커스터마이징된 ObjectMapper
   */
  public ObjectMapper rabbitObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }
}
