package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import com.athenhub.stockservice.stock.domain.event.external.StockRegisteredEvent;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 재고 생성 이벤트를 외부 시스템에 알리기 위해 RabbitMQ로 메시지를 발행하는 Publisher이다.
 *
 * <p>{@link StockCreatedEvent} (내부 이벤트)를 수신하여 {@link StockRegisteredEvent} (외부 이벤트)로 변환 후, 지정된
 * Exchange로 메시지를 전송한다.
 *
 * <p><b>트랜잭션 전략</b>
 *
 * <ul>
 *   <li>{@link TransactionalEventListener} + {@code AFTER_COMMIT}를 사용하여
 *   <li>DB 트랜잭션이 정상 커밋된 이후에만 메시지를 발행한다
 *   <li>따라서 롤백 시에는 RabbitMQ로 이벤트가 발행되지 않는다
 * </ul>
 *
 * <p><b>역할 분리</b>
 *
 * <ul>
 *   <li>도메인 내부에서는 {@code StockCreatedEvent}만 사용
 *   <li>외부 통신 시에는 {@code StockRegisteredEvent}로 변환하여 사용
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class StockRegisteredEventPublisher {

  /** RabbitMQ 메시지를 전송하기 위한 템플릿. */
  private final RabbitTemplate rabbitTemplate;

  /** stock 관련 exchange, routingKey 등의 설정 정보. */
  private final RabbitStockProperties stockProperties;

  /**
   * 재고 생성 이벤트를 수신하여 외부 메시지로 발행한다.
   *
   * <p>트랜잭션이 커밋된 이후에만 실행된다.
   *
   * @param event 도메인 내부에서 발생한 재고 생성 이벤트
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void publish(StockCreatedEvent event) {

    // 내부 이벤트 → 외부 이벤트로 변환
    StockRegisteredEvent message = StockRegisteredEvent.from(event);

    // RabbitMQ로 메시지 전송
    rabbitTemplate.convertAndSend(stockProperties.getExchange(), stockProperties.getRegistered().getRoutingKey(), message);
  }
}
