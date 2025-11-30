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

/**
 * 주문 생성 이벤트를 수신하여 재고 감소 처리를 위한 Batch 이벤트로 변환하는 RabbitMQ Listener이다.
 *
 * <p>Order 서비스에서 발행한 {@code OrderCreatedEvent}를 받아 재고 서비스 내부의 stock.decrease 큐로 전달할 수 있는 {@link
 * StockDecreaseBatchEvent}로 변환한다. 정상 처리 시 ACK을 수행하고, 예외 발생 시 원본 메시지를 DLQ로 이동시킨 후
 * NACK(requeue=false)을 수행한다.
 *
 * <p>핵심 역할:
 *
 * <ul>
 *   <li>OrderCreatedEvent → StockDecreaseBatchEvent 변환
 *   <li>재고 감소 이벤트 큐(stock.decrease)로 발행
 *   <li>예외 발생 시 DLQ 이동 및 재처리 방지
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedRabbitListener {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties stockProperties;

  /**
   * 주문 생성 이벤트를 수신하고 재고 감소 Batch 이벤트로 변환하여 내부 큐로 전달한다.
   *
   * <p>정상 처리 시 ACK을 수행하며, 처리 중 예외가 발생하면 DLQ로 메시지를 이동시키고 원본 메시지에 대해 NACK(requeue=false)을 호출한다.
   *
   * @param event 수신된 주문 생성 이벤트.
   * @param rawMessage RabbitMQ가 전달한 원본 메시지.
   * @param channel ACK/NACK 제어를 위한 AMQP 채널.
   * @param tag 메시지의 delivery tag.
   * @throws IOException ACK/NACK 처리 중 발생할 수 있는 예외.
   */
  @RabbitListener(queues = "${rabbit.order.created.queue}", containerFactory = "manualAckFactory")
  public void listen(
      OrderCreatedEvent event,
      Message rawMessage,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException {

    final String queueName = rawMessage.getMessageProperties().getConsumerQueue();
    final Object orderId = event.orderId();

    log.info(
        "[RECEIVED] queue={}, tag={}, orderId={}, products={}",
        queueName,
        tag,
        orderId,
        event.products().size());

    try {
      StockDecreaseBatchEvent batchEvent = createBatchEvent(event);
      publishBatchEvent(batchEvent, orderId);
      ack(channel, tag, orderId);

    } catch (Exception ex) {
      log.error(
          "[ERROR] OrderCreatedEvent 처리 실패 orderId={}, cause={}", orderId, ex.getMessage(), ex);

      handleFailure(rawMessage, orderId);
      nack(channel, tag, orderId);
    }
  }

  /**
   * 주문 생성 이벤트를 재고 감소 Batch 이벤트로 변환한다.
   *
   * @param event 주문 생성 이벤트.
   * @return 변환된 {@link StockDecreaseBatchEvent}.
   */
  private StockDecreaseBatchEvent createBatchEvent(OrderCreatedEvent event) {
    List<StockDecreaseRequest> requests =
        event.products().stream()
            .map(
                p ->
                    new StockDecreaseRequest(
                        p.productId(), p.variantId(), p.quantity(), event.orderedAt()))
            .toList();

    log.info("[PROCESS] BatchEvent 생성 orderId={}, items={}", event.orderId(), requests.size());

    return new StockDecreaseBatchEvent(event.orderId(), event.orderedAt(), requests);
  }

  /**
   * 변환된 Batch 이벤트를 stock.decrease 큐로 발행한다.
   *
   * @param batch 변환된 Batch 이벤트.
   * @param orderId 주문 ID.
   */
  private void publishBatchEvent(StockDecreaseBatchEvent batch, Object orderId) {
    final String routingKey = stockProperties.getDecrease().getRoutingKey();

    rabbitTemplate.convertAndSend(stockProperties.getExchange(), routingKey, batch);

    log.info("[SEND] stock.decrease 발행 성공 orderId={}, routingKey={}", orderId, routingKey);
  }

  /**
   * 실패한 메시지를 DLQ로 이동시킨다.
   *
   * @param rawMessage 원본 RabbitMQ 메시지.
   * @param orderId 주문 ID.
   */
  private void handleFailure(Message rawMessage, Object orderId) {
    final String dlqRoutingKey = stockProperties.getDecreaseDead().getRoutingKey();

    try {
      rabbitTemplate.send(stockProperties.getDlqExchange(), dlqRoutingKey, rawMessage);

      log.warn("[DLQ] 이동 성공 orderId={}, routingKey={}", orderId, dlqRoutingKey);

    } catch (Exception ex) {
      log.error("[DLQ ERROR] 이동 실패 orderId={}, cause={}", orderId, ex.getMessage(), ex);
    }
  }

  /**
   * 메시지를 정상 처리 후 ACK한다.
   *
   * @param channel AMQP 채널.
   * @param tag delivery tag.
   * @param orderId 주문 ID.
   * @throws IOException ACK 처리 중 발생할 수 있는 예외.
   */
  private void ack(Channel channel, long tag, Object orderId) throws IOException {
    channel.basicAck(tag, false);
    log.info("[ACK] 처리 성공 orderId={}, tag={}", orderId, tag);
  }

  /**
   * 메시지를 재처리하지 않도록 NACK(requeue=false) 한다.
   *
   * @param channel AMQP 채널.
   * @param tag delivery tag.
   * @param orderId 주문 ID.
   * @throws IOException NACK 처리 중 발생할 수 있는 예외.
   */
  private void nack(Channel channel, long tag, Object orderId) throws IOException {
    channel.basicNack(tag, false, false);
    log.info("[NACK] 처리 실패(재처리 없음) orderId={}, tag={}", orderId, tag);
  }
}
