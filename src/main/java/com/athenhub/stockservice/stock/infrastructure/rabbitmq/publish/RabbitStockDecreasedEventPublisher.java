package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import com.athenhub.stockservice.stock.application.service.StockDecreasedEventPublisher;
import com.athenhub.stockservice.stock.domain.event.internal.StockDecreasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockDecreasedEventPublisher implements StockDecreasedEventPublisher {

  private final RabbitTemplate rabbitTemplate;

  private final RabbitStockProperties stockProperties;

  public void publish(StockDecreasedEvent event) {
    // RabbitMQ로 메시지 전송
    rabbitTemplate.convertAndSend(
        stockProperties.getExchange(), stockProperties.getDecreased().getRoutingKey(), event);
  }
}
