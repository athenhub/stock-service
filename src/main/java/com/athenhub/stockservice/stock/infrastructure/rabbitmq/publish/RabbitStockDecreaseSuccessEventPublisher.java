package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import com.athenhub.stockservice.stock.application.event.external.StockDecreaseSuccessEvent;
import com.athenhub.stockservice.stock.application.service.StockDecreaseSuccessEventPublisher;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock.RabbitStockProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 재고 감소 성공 시 RabbitMQ로 성공 이벤트를 발행하는 Publisher 구현체이다.
 *
 * <p>재고 감소 성공 이벤트({@link StockDecreaseSuccessEvent})는 주문 서비스 또는 다른 시스템이 후속 로직(예: 주문 상태 업데이트, 결제 진행
 * 등)을 수행할 수 있도록 전달된다.
 *
 * <p>이 구현체는 StockDecreaseSuccessEventPublisher 인터페이스를 기반으로 하며, 선언된 Exchange와 Routing Key를 사용하여 메시지를
 * 발송한다.
 *
 * <p>주요 책임:
 *
 * <ul>
 *   <li>RabbitMQ에 성공 이벤트 발행
 *   <li>재고 처리 파이프라인의 최종 성공 여부 전달
 *   <li>메시지 발행 시 설정된 Exchange/Routing Key 사용
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class RabbitStockDecreaseSuccessEventPublisher
    implements StockDecreaseSuccessEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitStockProperties stockProperties;

  /**
   * 재고 감소 성공 이벤트를 RabbitMQ로 발행한다.
   *
   * <p>해당 이벤트는 재고 감소가 정상적으로 완료된 이후 호출되며, 메시지는 stockProperties에 정의된 exchange 및 decreaseSuccess
   * routing key를 통해 발송된다.
   *
   * @param event 재고 감소 성공 이벤트 Payload
   */
  @Override
  public void publish(StockDecreaseSuccessEvent event) {
    rabbitTemplate.convertAndSend(
        stockProperties.getExchange(), stockProperties.getDecreaseSuccess().getRoutingKey(), event);
  }
}
