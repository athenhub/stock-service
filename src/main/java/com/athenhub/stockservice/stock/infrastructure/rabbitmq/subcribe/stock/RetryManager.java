package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.stock;

import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock.RabbitStockProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 재고 감소 메시지의 재시도(Retry) 및 DLQ(최종 실패) 전송을 관리하는 유틸리티 클래스.
 *
 * <p>역할:
 *
 * <ul>
 *   <li>메시지의 retry-count 읽기/증가
 *   <li>최종 실패 시 DLQ Exchange로 메시지 publish
 *   <li>역직렬화 실패 등 RAW 메시지를 DLQ로 직접 전송
 * </ul>
 *
 * <p>비즈니스 로직과 Listener에서 재시도 제어 로직을 분리하여 책임을 명확히 한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class RetryManager {

  private static final String RETRY_COUNT_HEADER_NAME = "x-retry-count";

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties props;

  /**
   * 메시지의 retry-count 값을 읽는다.
   *
   * <p>없으면 0을 반환한다.
   */
  public int getRetryCount(Message message) {
    Object retry = message.getMessageProperties().getHeader(RETRY_COUNT_HEADER_NAME);
    if (retry instanceof Number n) {
      return n.intValue();
    }
    return 0;
  }

  /** 메시지의 retry-count를 +1 증가시킨다. */
  public void increaseRetryCount(Message message) {
    int retry = getRetryCount(message);
    message.getMessageProperties().setHeader(RETRY_COUNT_HEADER_NAME, retry + 1);
  }

  /** 재시도 N회 초과 등으로 더 이상 처리할 수 없는 메시지를 DLQ Exchange로 publish 한다. */
  public void sendToDlq(Object payload, int retryCount) {
    rabbitTemplate.convertAndSend(
        props.getDlqExchange(), // 🔥 DLQ 전용 Exchange로 수정
        props.getDecreaseDead().getRoutingKey(),
        payload,
        msg -> {
          msg.getMessageProperties().setHeader(RETRY_COUNT_HEADER_NAME, retryCount);
          return msg;
        });
  }

  /** 역직렬화 실패 등 RAW 메시지를 그대로 DLQ로 보낸다. */
  public void sendRawToDlq(Message raw) {
    rabbitTemplate.send(
        props.getDlqExchange(), // 🔥 DLQ Exchange로 수정
        props.getDecreaseDead().getRoutingKey(),
        raw);
  }
}
