package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.order;

import com.athenhub.commoncore.message.MessageResolver;
import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.athenhub.stockservice.stock.application.dto.StockDecreaseRequest;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.order.RabbitOrderProperties;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.event.OrderCreatedEvent;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.event.OrderProcessFailedEvent;
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
 * Order 서비스에서 발행한 {@code OrderCreatedEvent}를 수신하여 재고 감소 요청을 위한 {@link StockDecreaseBatchEvent}로
 * 변환하는 Listener이다.
 *
 * <p>정상 처리 시 stock.decrease 큐로 이벤트를 발행하고 ACK 처리한다. 변환 또는 발행 과정에서 예외가 발생하면 Order 도메인의 실패 이벤트({@link
 * OrderProcessFailedEvent})를 발행하고 원본 메시지는 NACK(requeue=false) 처리한다.
 *
 * <p>이 Listener는 "주문 생성 성공 이후 재고 감소 요청"의 Saga Step 1을 담당한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedRabbitListener {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitOrderProperties orderProperties;

  private final MessageResolver messageResolver;

  @RabbitListener(queues = "${rabbit.order.created.queue}", containerFactory = "manualAckFactory")
  public void listen(
      OrderCreatedEvent event,
      Message rawMessage,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException {

    final var orderId = event.orderId();

    log.info("[RECEIVED] orderId={}, products={}", orderId, event.products().size());

    try {
      StockDecreaseBatchEvent batch = createBatchEvent(event);
      publishBatchEvent(batch, orderId);
      ack(channel, tag, orderId);

    } catch (Exception ex) {
      log.error("[ERROR] 주문 생성 이벤트 처리 실패 orderId={}, cause={}", orderId, ex.getMessage(), ex);

      publishProcessFailedEvent(event);
      nack(channel, tag, orderId);
    }
  }

  /** 주문 생성 이벤트 → 재고 감소 Batch 이벤트 변환 */
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

  /** 재고 감소 Batch 이벤트를 stock.decrease 큐로 발행 */
  private void publishBatchEvent(StockDecreaseBatchEvent batch, Object orderId) {
    rabbitTemplate.convertAndSend(
        orderProperties.getExchange(), orderProperties.getCreated().getRoutingKey(), batch);

    log.info("[SEND] stock.decrease 발행 성공 orderId={}", orderId);
  }

  /** 주문 생성 이벤트 처리 실패 시 OrderProcessFailedEvent 발행 */
  private void publishProcessFailedEvent(OrderCreatedEvent event) {
    String errorCode = "ORDER_CREATED_EVENT_PROCESS_FAILED";
    OrderProcessFailedEvent failed =
        new OrderProcessFailedEvent(event.orderId(), errorCode, messageResolver.resolve(errorCode));

    rabbitTemplate.convertAndSend(
        orderProperties.getExchange(), orderProperties.getProcessFailed().getRoutingKey(), failed);

    log.warn("[SEND] OrderProcessFailedEvent 발행 orderId={}", event.orderId());
  }

  private void ack(Channel channel, long tag, Object orderId) throws IOException {
    channel.basicAck(tag, false);
    log.info("[ACK] orderId={}", orderId);
  }

  private void nack(Channel channel, long tag, Object orderId) throws IOException {
    channel.basicNack(tag, false, false);
    log.info("[NACK] orderId={}", orderId);
  }
}
