package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.stock;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.athenhub.stockservice.stock.application.service.StockDecreaseHandler;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitStockDecreaseEventListener {

  private static final int RETRY_MAX = 5;

  private final RetryManager retryManager;
  private final StockDecreaseHandler handler;

  @RabbitListener(queues = "${rabbit.stock.decrease.queue}", containerFactory = "manualAckFactory")
  public void listen(
      StockDecreaseBatchEvent event,
      Message rawMessage,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException {

    final String queueName = rawMessage.getMessageProperties().getConsumerQueue();
    final int retry = retryManager.getRetryCount(rawMessage);

    log.info("[RECEIVED] queue={} retry={} orderId={}", queueName, retry, event.orderId());

    try {
      log.info(
          "[PROCESS] 재고 감소 처리 시작 orderId={} items={}",
          event.orderId(),
          event.stockDecreaseRequests().size());

      handler.decreaseAll(event.orderId(), event.stockDecreaseRequests());

      log.info("[SUCCESS] 재고 감소 완료 orderId={}", event.orderId());

      channel.basicAck(tag, false);
      log.info("[ACK] 메시지 정상 처리 완료 orderId={} tag={}", event.orderId(), tag);
    } catch (Exception ex) {

      log.error(
          "[ERROR] 재고 감소 실패 orderId={} retry={} cause={}",
          event.orderId(),
          retry,
          ex.getMessage(),
          ex);

      int nextRetry = retry + 1;

      // 1) 최대 재시도 초과 → DLQ
      if (nextRetry > RETRY_MAX) {
        log.warn("[DLQ] 재시도 초과({}) → DLQ 이동 orderId={}", RETRY_MAX, event.orderId());

        retryManager.sendToDlq(event, retry);
        channel.basicAck(tag, false);

        log.info("[ACK] DLQ 전송 후 ACK 처리 완료 orderId={} tag={}", event.orderId(), tag);
        return;
      }

      // 2) retry 가능한 예외 → retry 큐 재발행
      log.warn("[RETRY] 재고 감소 재시도 준비 nextRetry={} orderId={}", nextRetry, event.orderId());

      retryManager.sendToRetry(event, nextRetry);

      channel.basicAck(tag, false);
      log.info("[ACK] 원본 메시지 ACK 처리 완료 (retry 발행 완료) orderId={} tag={}", event.orderId(), tag);
    }
  }
}
