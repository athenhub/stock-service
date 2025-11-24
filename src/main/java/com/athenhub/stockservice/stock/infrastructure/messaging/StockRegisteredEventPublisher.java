package com.athenhub.stockservice.stock.infrastructure.messaging;

import com.athenhub.stockservice.global.infrastructure.rabbitmq.RabbitStockProperties;
import com.athenhub.stockservice.stock.domain.event.external.StockRegisteredEvent;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class StockRegisteredEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties stockProperties;

  /** DB 커밋 이후 외부 이벤트를 RabbitMQ로 전송한다. */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void publish(StockCreatedEvent event) {

    StockRegisteredEvent message = StockRegisteredEvent.from(event);

    rabbitTemplate.convertAndSend(stockProperties.getExchange(), "stock.registered", message);
  }
}
