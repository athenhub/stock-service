package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.stock;

import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock.RabbitStockProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Retry / DLQ 메시지 발행 관리자. - retryCount 헤더는 오직 재발행 시에만 반영됨. */
@Component
@RequiredArgsConstructor
public class RetryManager {

  private static final String RETRY = "x-retry-count";

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties props;

  public int getRetryCount(Message msg) {
    Object retry = msg.getMessageProperties().getHeader(RETRY);
    return retry instanceof Number n ? n.intValue() : 0;
  }

  /** 재시도 재발행 (retryCount 포함) */
  public void sendToRetry(Object payload, int retryCount) {
    rabbitTemplate.convertAndSend(
        props.getExchange(),
        props.getDecreaseRetry().getRoutingKey(),
        payload,
        msg -> {
          msg.getMessageProperties().setHeader(RETRY, retryCount);
          return msg;
        });
  }

  /** DLQ 재발행 */
  public void sendToDlq(Object payload, int retryCount) {
    rabbitTemplate.convertAndSend(
        props.getDlqExchange(),
        props.getDecreaseDead().getRoutingKey(),
        payload,
        msg -> {
          msg.getMessageProperties().setHeader(RETRY, retryCount);
          return msg;
        });
  }
}
