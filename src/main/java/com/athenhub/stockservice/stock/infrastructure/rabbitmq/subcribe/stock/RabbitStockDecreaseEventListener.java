package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.stock;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.athenhub.stockservice.stock.application.service.StockDecreaseHandler;
import com.athenhub.stockservice.stock.domain.exception.InsufficientStockException;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.error.StockErrorType;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ로부터 전달받은 재고 감소 이벤트를 처리하는 소비자(Consumer)이다.
 *
 * <p>이 리스너는 manual ACK 모드를 사용하여 메시지 처리의 성공/실패에 따라 직접 ACK를 제어한다. 처리 흐름은 아래와 같다:
 *
 * <ul>
 *   <li><b>정상 처리 성공</b>: 비즈니스 로직 성공 후 ACK.
 *   <li><b>재고 부족 예외</b>: 재시도가 의미 없으므로 즉시 DLQ로 전달 후 ACK.
 *   <li><b>기타 예외</b>: retryCount가 최대 재시도 횟수 이하일 경우 retry queue로 재발행 후 ACK.
 *   <li><b>재시도 초과</b>: DLQ로 이동 후 ACK.
 * </ul>
 *
 * <p>이 구조는 소비자의 책임을 명확히 하며, 메시지 유실 없이 안정적으로 메시지를 처리할 수 있도록 한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitStockDecreaseEventListener {

  /** 재시도 가능한 최대 횟수. */
  private static final int RETRY_MAX = 5;

  private final RetryManager retryManager;
  private final StockDecreaseHandler handler;

  /**
   * 재고 감소 이벤트 메시지를 수신하여 처리한다.
   *
   * <p>이 메서드는 다음과 같은 역할을 수행한다:
   *
   * <ul>
   *   <li>메시지의 retryCount 조회.
   *   <li>재고 감소 처리 시도.
   *   <li>예외 유형에 따른 retry 또는 DLQ 전략 수행.
   * </ul>
   *
   * @param event 메시지 페이로드로 전달된 재고 감소 요청 데이터.
   * @param rawMessage RabbitMQ 원본 메시지.
   * @param channel 수동 ACK 처리를 위한 채널.
   * @param tag 메시지의 delivery tag.
   * @throws IOException ACK 처리 중 오류가 발생할 수 있다.
   */
  @RabbitListener(queues = "${rabbit.stock.decrease.queue}", containerFactory = "manualAckFactory")
  public void listen(
      StockDecreaseBatchEvent event,
      Message rawMessage,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException {

    final int retry = retryManager.getRetryCount(rawMessage);
    final String queueName = rawMessage.getMessageProperties().getConsumerQueue();

    log.info("[RECEIVED] queue={}, retry={}, orderId={}", queueName, retry, event.orderId());

    try {
      processDecrease(event);
      ackSuccess(channel, tag, event.orderId());

    } catch (InsufficientStockException ex) {
      handleOutOfStock(event, retry, ex, channel, tag);

    } catch (Exception ex) {
      handleRetryOrDlq(event, retry, ex, channel, tag);
    }
  }

  /**
   * 재고 감소 비즈니스 로직을 처리한다.
   *
   * <p>handler.decreaseAll() 실행 과정에서 예외 발생 시 상위에서 retry 또는 DLQ 전략을 수행한다.
   *
   * @param event 재고 감소 요청 이벤트.
   */
  private void processDecrease(StockDecreaseBatchEvent event) {
    log.info(
        "[PROCESS] orderId={}, items={}", event.orderId(), event.stockDecreaseRequests().size());

    handler.decreaseAll(event.orderId(), event.stockDecreaseRequests());

    log.info("[SUCCESS] orderId={}", event.orderId());
  }

  /**
   * 재고 부족 예외가 발생한 경우 수행되는 처리이다.
   *
   * <p>재고 부족은 재시도가 의미 없으므로 즉시 DLQ로 전달하며, 이후 메시지를 ACK하여 원본 메시지를 큐에서 제거한다.
   *
   * @param event 처리 중인 이벤트.
   * @param retry 현재 retryCount.
   * @param ex 예외 정보.
   * @param channel ACK 처리를 위한 채널.
   * @param tag delivery tag.
   * @throws IOException ACK 처리 중 오류가 발생할 수 있다.
   */
  private void handleOutOfStock(
      StockDecreaseBatchEvent event,
      int retry,
      InsufficientStockException ex,
      Channel channel,
      long tag)
      throws IOException {

    log.error("[OUT_OF_STOCK] orderId={}, reason={}", event.orderId(), ex.getMessage());

    retryManager.sendToDlq(event, retry, StockErrorType.OUT_OF_STOCK);

    basicAck(channel, tag);
    log.info("[ACK] status=dlq_out_of_stock, orderId={}, tag={}", event.orderId(), tag);
  }

  /**
   * 일반 예외 발생 시 retry 또는 DLQ로 처리 방향을 결정한다.
   *
   * <p>retryCount가 RETRY_MAX 이하라면 retry queue로 재발행하고, 초과 시 즉시 DLQ로 이동한다.
   *
   * @param event 이벤트 페이로드.
   * @param retry 현재 retryCount.
   * @param ex 발생한 예외.
   * @param channel ACK 처리를 위한 채널.
   * @param tag delivery tag.
   * @throws IOException ACK 처리 중 오류가 발생할 수 있다.
   */
  private void handleRetryOrDlq(
      StockDecreaseBatchEvent event, int retry, Exception ex, Channel channel, long tag)
      throws IOException {

    log.error(
        "[ERROR] orderId={}, retry={}, cause={}", event.orderId(), retry, ex.getMessage(), ex);

    int nextRetry = retry + 1;

    if (nextRetry > RETRY_MAX) {
      sendToDlqExceeded(event, retry, channel, tag);
      return;
    }

    sendToRetry(event, nextRetry, channel, tag);
  }

  /**
   * retryCount가 허용 범위 내인 경우 retry queue로 메시지를 재발행한다. 이후 원본 메시지는 ACK 처리하여 큐에서 제거한다.
   *
   * @param event 이벤트 페이로드.
   * @param nextRetry 증가된 retryCount.
   * @param channel ACK용 채널.
   * @param tag delivery tag.
   * @throws IOException ACK 중 오류.
   */
  private void sendToRetry(StockDecreaseBatchEvent event, int nextRetry, Channel channel, long tag)
      throws IOException {

    log.warn("[RETRY] orderId={}, nextRetry={}", event.orderId(), nextRetry);

    retryManager.sendToRetry(event, nextRetry);

    basicAck(channel, tag);
    log.info("[ACK] status=retry_published, orderId={}, tag={}", event.orderId(), tag);
  }

  /**
   * 재시도 횟수를 초과한 경우 DLQ로 메시지를 이동시키고 ACK 처리한다.
   *
   * @param event 이벤트 페이로드.
   * @param retry 현재 retryCount.
   * @param channel ACK 채널.
   * @param tag delivery tag.
   * @throws IOException ACK 중 오류.
   */
  private void sendToDlqExceeded(
      StockDecreaseBatchEvent event, int retry, Channel channel, long tag) throws IOException {

    log.warn("[DLQ] reason=retry_exceeded, retryMax={}, orderId={}", RETRY_MAX, event.orderId());

    retryManager.sendToDlq(event, retry, StockErrorType.RETRY_EXCEEDED);

    basicAck(channel, tag);
    log.info("[ACK] status=dlq_retry_exceeded, orderId={}, tag={}", event.orderId(), tag);
  }

  /**
   * 정상 처리 완료 후 ACK 및 로그를 남긴다.
   *
   * @param channel ACK 처리 채널.
   * @param tag delivery tag.
   * @param orderId 처리된 주문 ID.
   * @throws IOException ACK 중 오류.
   */
  private void ackSuccess(Channel channel, long tag, Object orderId) throws IOException {
    basicAck(channel, tag);
    log.info("[ACK] status=success, orderId={}, tag={}", orderId, tag);
  }

  /**
   * RabbitMQ 메시지를 단건 ACK 처리한다.
   *
   * <p>multiple=false 전략을 사용하여 해당 메시지에 대해서만 ACK한다.
   *
   * @param channel 채널.
   * @param tag delivery tag.
   * @throws IOException ACK 중 오류.
   */
  private void basicAck(Channel channel, long tag) throws IOException {
    channel.basicAck(tag, false);
  }
}
