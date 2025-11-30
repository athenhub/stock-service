package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.order;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.athenhub.stockservice.stock.application.dto.StockDecreaseRequest;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock.RabbitStockProperties;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedRabbitListener {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties stockProperties;

  @RabbitListener(queues = "${rabbit.order.created.queue}", containerFactory = "manualAckFactory")
  public void listen(
      OrderCreatedEvent event,
      Message rawMessage,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException {

    final String queueName = rawMessage.getMessageProperties().getConsumerQueue();

    log.info(
        "[RECEIVED] queue={} tag={} orderId={} products={}",
        queueName,
        tag,
        event.orderId(),
        event.products().size());

    try {
      // 1. StockDecreaseRequest 변환
      log.info(
          "[PROCESS] 주문 생성 이벤트 변환 시작 orderId={} productCount={}",
          event.orderId(),
          event.products().size());

      List<StockDecreaseRequest> stockDecreaseRequests =
          event.products().stream()
              .map(
                  product ->
                      new StockDecreaseRequest(
                          product.productId(),
                          product.variantId(),
                          product.quantity(),
                          event.orderedAt()))
              .toList();

      // 2. Batch Event 생성
      StockDecreaseBatchEvent batchEvent =
          new StockDecreaseBatchEvent(event.orderId(), event.orderedAt(), stockDecreaseRequests);

      log.info(
          "[PROCESS] BatchEvent 생성 완료 orderId={} items={}",
          event.orderId(),
          stockDecreaseRequests.size());

      // 3. 내부 큐(stock.decrease)로 전달
      rabbitTemplate.convertAndSend(
          stockProperties.getExchange(), stockProperties.getDecrease().getRoutingKey(), batchEvent);

      log.info(
          "[SEND] stock.decrease 큐로 재고 감소 BatchEvent 발행 완료 orderId={} routingKey={}",
          event.orderId(),
          stockProperties.getDecrease().getRoutingKey());

      // 4. ACK
      channel.basicAck(tag, false);
      log.info("[ACK] OrderCreatedEvent 정상 처리 완료 orderId={} tag={}", event.orderId(), tag);
    } catch (Exception ex) {
      log.error(
          "[ERROR] OrderCreatedEvent 처리 실패 → DLQ 이동 예정 orderId={} cause={}",
          event.orderId(),
          ex.getMessage(),
          ex);

      try {
        rabbitTemplate.send(
            stockProperties.getDlqExchange(),
            stockProperties.getDecreaseDead().getRoutingKey(),
            rawMessage);

        log.warn(
            "[DLQ] 원본 메시지를 DLQ로 이동 완료 orderId={} dlqRoutingKey={}",
            event.orderId(),
            stockProperties.getDecreaseDead().getRoutingKey());
      } catch (Exception dlqEx) {
        log.error(
            "[DLQ ERROR] DLQ 이동 중 오류 발생 orderId={} cause={}",
            event.orderId(),
            dlqEx.getMessage(),
            dlqEx);
      }

      // 5. NACK(requeue=false)
      channel.basicNack(tag, false, false);
      log.info("[NACK] DLQ 이동 후 원본 메시지 NACK 처리 완료 orderId={} tag={}", event.orderId(), tag);
    }
  }
}
