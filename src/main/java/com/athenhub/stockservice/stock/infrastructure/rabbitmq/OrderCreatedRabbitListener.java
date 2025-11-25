package com.athenhub.stockservice.stock.infrastructure.rabbitmq;

import com.athenhub.stockservice.stock.application.evenhandler.StockDecreaseHandler;
import com.athenhub.stockservice.stock.domain.dto.StockDecreaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * OrderCreatedEvent를 RabbitMQ로부터 수신하는 리스너이다.
 */
@Component
@RequiredArgsConstructor
public class OrderCreatedRabbitListener {

  private final StockDecreaseHandler stockDecreaseHandler;

  @RabbitListener(queues = "${rabbitmq.queue.order-created}")
  public void listen(OrderCreatedEvent event) {
    StockDecreaseRequest stockDecreaseRequest =
        new StockDecreaseRequest(
            event.productId(), event.variantId(), event.quantity(), event.orderedAt());
    stockDecreaseHandler.decrease(stockDecreaseRequest);
  }
}
