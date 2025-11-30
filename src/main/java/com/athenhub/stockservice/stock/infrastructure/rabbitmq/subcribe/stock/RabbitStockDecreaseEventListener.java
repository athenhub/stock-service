package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.stock;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.athenhub.stockservice.stock.application.service.StockDecreaseHandler;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.parser.StockDecreaseMessageParser;
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

  private static final int RETRY_COUNT = 5;

  private final StockDecreaseMessageParser parser;
  private final RetryManager retryManager;
  private final StockDecreaseHandler handler;

  @RabbitListener(queues = "${rabbit.stock.decrease.queue}")
  public void listen(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException {

    int retry = retryManager.getRetryCount(message);

    // 1. 역직렬화
    StockDecreaseBatchEvent event;
    try {
      event = parser.parse(message);
    } catch (Exception ex) {
      log.error("역직렬화 실패 → DLQ", ex);
      retryManager.sendRawToDlq(message);
      channel.basicAck(tag, false);
      return;
    }

    // 2. 비즈니스 로직 실행
    try {
      handler.decreaseAll(event.orderId(), event.stockDecreaseRequests());
      channel.basicAck(tag, false);
    } catch (Exception ex) {
      log.error("재고 감소 실패 retry={}, event={}", retry, event, ex);

      if (retry >= RETRY_COUNT) {
        retryManager.sendToDlq(event, retry);
        channel.basicAck(tag, false);
        return;
      }

      retryManager.increaseRetryCount(message);
      channel.basicReject(tag, false);
    }
  }
}
