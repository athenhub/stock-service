package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import com.athenhub.stockservice.stock.application.service.StockDecreaseFailedEventPublisher;
import com.athenhub.stockservice.stock.domain.event.external.StockDecreaseFailedEvent;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock.RabbitStockProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 재고 감소 실패 이벤트를 RabbitMQ로 발행하는 구현체이다.
 *
 * <p>{@link StockDecreaseFailedEventPublisher}를 구현하여, 재고 감소 처리 중 실패가 발생했음을 외부 시스템(Order 서비스 등)에
 * 전달한다.
 *
 * <p>Exchange 및 Routing Key 정보는 {@link RabbitStockProperties}를 통해 주입받는다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockDecreaseFailedEventPublisher implements StockDecreaseFailedEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties stockProperties;

  /**
   * 재고 감소 실패 이벤트를 RabbitMQ로 발행한다.
   *
   * <p>지정된 Exchange와 Routing Key를 사용하여 {@link StockDecreaseFailedEvent}를 메시지로 전송한다.
   *
   * @param event 재고 감소 실패 이벤트
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public void publish(StockDecreaseFailedEvent event) {
    rabbitTemplate.convertAndSend(
        stockProperties.getExchange(), stockProperties.getDecreaseDead().getRoutingKey(), event);
  }
}
