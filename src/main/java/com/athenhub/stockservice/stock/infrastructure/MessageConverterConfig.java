package com.athenhub.stockservice.stock.infrastructure;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 메시지 직렬화/역직렬화를 위한 설정 클래스이다.
 *
 * <p>메시지를 JSON 형식으로 변환하기 위해 {@link Jackson2JsonMessageConverter}를 Bean으로 등록한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
public class MessageConverterConfig {

  /**
   * JSON 기반 메시지 변환기를 등록한다.
   *
   * <p>Producer가 발행하는 객체를 JSON으로 직렬화하고, Consumer가 수신하는 JSON 메시지를 객체로 역직렬화하는 데 사용된다.
   *
   * @return Jackson2JsonMessageConverter
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
