package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import com.athenhub.stockservice.stock.application.service.StockRegisteredEventPublisher;
import com.athenhub.stockservice.stock.domain.event.external.StockRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockRegisteredEventPublisher implements StockRegisteredEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties stockProperties;

  @Override
  public void publish(StockRegisteredEvent event) {
    rabbitTemplate.convertAndSend(
        stockProperties.getExchange(), stockProperties.getRegistered().getRoutingKey(), event);
  }
}
