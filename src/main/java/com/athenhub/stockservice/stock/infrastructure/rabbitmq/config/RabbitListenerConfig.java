package com.athenhub.stockservice.stock.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Listener Container 설정 클래스.
 *
 * <p>기본적으로 Spring AMQP는 Listener 컨테이너를 자동 구성하며, acknowledge-mode=AUTO 를 사용해 메시지를 자동으로 ACK 처리한다.
 *
 * <p>그러나 재고 감소(StockDecrease)처럼 재시도/딜레이 큐/최종 DLQ 처리가 필요한 경우 메시지를 직접 ACK/NACK 해야 하므로 MANUAL ACK 모드가
 * 필요하다.
 *
 * <p>이 설정은 'manualAckFactory' 라는 컨테이너 팩토리를 생성하여 MANUAL ACK 환경을 제공하며, 특정 @RabbitListener 에서
 * containerFactory="manualAckFactory" 를 지정한 경우에만 사용된다.
 *
 * <p>따라서 전체 앱이 MANUAL ACK로 바뀌는 것이 아니라, "재고 감소 리스너" 등 필요 큐에서만 수동 ACK 제어를 적용할 수 있도록 한다.
 *
 * <p>주요 설정:
 *
 * <ul>
 *   <li>AcknowledgeMode.MANUAL — 메시지 ACK/NACK 을 개발자가 직접 수행
 *   <li>PrefetchCount=1 — 메시지를 한 번에 하나씩 처리하여 중복처리/경쟁 조건 방지
 *   <li>defaultRequeueRejected=false — reject 시 큐로 되돌아가지 않고 DLX 로 이동
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
public class RabbitListenerConfig {

  /**
   * MANUAL ACK 모드의 RabbitListenerContainerFactory.
   *
   * <p>수동 ACK 환경을 구성하기 위한 컨테이너 팩토리이며, @RabbitListener(containerFactory = "manualAckFactory") 에서만
   * 적용된다.
   *
   * @param connectionFactory RabbitMQ ConnectionFactory
   * @return MANUAL ACK 전용 컨테이너 팩토리
   */
  @Bean
  public SimpleRabbitListenerContainerFactory manualAckFactory(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jacksonMessageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jacksonMessageConverter);
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    factory.setPrefetchCount(1);
    factory.setDefaultRequeueRejected(false);
    return factory;
  }
}
