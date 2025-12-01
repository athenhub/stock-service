package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.stock;

import com.athenhub.commoncore.message.MessageResolver;
import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.order.RabbitOrderProperties;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.event.OrderProcessFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 재고 감소 처리 실패로 인해 DLQ(Dead Letter Queue)에 적재된 메시지를 수신하여 주문 서비스에 처리 실패 이벤트를 전달하는 Listener이다.
 *
 * <p>이 Listener는 DLQ의 메시지를 소비하지만 ACK/NACK을 호출하지 않으므로 DLQ의 원본 메시지는 그대로 보존된다.
 *
 * <p>실패 유형은 메시지 헤더(x-error-type)에서 추출하며, MessageResolver를 사용하여 사람이 읽을 수 있는 설명 메시지로 변환한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class RabbitStockDecreasedDeadEventListener {

  private final RabbitOrderProperties orderProperties;
  private final RabbitTemplate rabbitTemplate;

  /** DLQ 메시지 헤더에서 실패 유형을 가져오기 위한 헤더 이름. */
  private static final String ERROR_TYPE_HEADER_NAME = "x-error-type";

  private final MessageResolver messageResolver;

  /**
   * 재고 감소 처리 중 오류가 발생하여 DLQ에 적재된 이벤트를 수신한다.
   *
   * <p>DLQ의 메시지를 기반으로 {@link OrderProcessFailedEvent}를 생성하여 주문 서비스가 후속 보상 로직(주문 취소, 결제 취소 등)을 수행할 수
   * 있도록 이벤트를 발행한다.
   *
   * <p>DLQ의 데이터를 보존해야 하므로 ACK/NACK을 호출하지 않는다.
   *
   * @param event DLQ에 저장된 원본 재고 감소 요청 이벤트
   * @param message Raw RabbitMQ 메시지(헤더 포함)
   * @author 김지원
   * @since 1.0.0
   */
  @RabbitListener(
      queues = "${rabbit.stock.decrease-dead.queue}",
      containerFactory = "manualAckFactory")
  public void listen(StockDecreaseBatchEvent event, Message message) {
    // 1) 실패 유형을 DLQ 메시지 헤더에서 추출
    String errorType = message.getMessageProperties().getHeader(ERROR_TYPE_HEADER_NAME);

    // 2) 실패 이벤트 생성 (e.g., 재고 부족, 낙관적 락 충돌 등)
    OrderProcessFailedEvent failedEvent =
        new OrderProcessFailedEvent(
            event.orderId(), errorType, messageResolver.resolve(errorType) // 사람이 읽을 수 있는 에러 메시지로 변환
            );

    // 3) 주문 서비스로 "주문 처리 실패" 이벤트 발행
    rabbitTemplate.convertAndSend(
        orderProperties.getExchange(),
        orderProperties.getProcessFailed().getRoutingKey(),
        failedEvent);

    // 4) ACK/NACK을 호출하지 않음 → DLQ 메시지 보존 목적
  }
}
