package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import com.athenhub.stockservice.stock.application.service.StockDecreasedEventPublisher;
import com.athenhub.stockservice.stock.domain.event.external.StockDecreasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 재고 감소 이벤트를 RabbitMQ로 발행하는 구현체이다.
 *
 * <p>{@link StockDecreasedEventPublisher}를 구현하여, 재고 감소가 완료되었음을 메시지 브로커를 통해 외부 시스템에 전달한다.
 *
 * <p>Exchange 와 RoutingKey 정보는 {@link RabbitStockProperties}를 통해 주입받는다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockDecreasedEventPublisher implements StockDecreasedEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties stockProperties;

  /**
   * 재고 감소 이벤트를 RabbitMQ로 발행한다.
   *
   * <p>지정된 Exchange와 Routing Key를 사용하여 {@link StockDecreasedEvent}를 메시지로 전송한다.
   *
   * @param event 재고 감소 이벤트
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public void publish(StockDecreasedEvent event) {
    rabbitTemplate.convertAndSend(
        stockProperties.getExchange(), stockProperties.getDecreased().getRoutingKey(), event);
  }
}
