package com.athenhub.stockservice.stock.infrastructure.rabbitmq.publish;

import com.athenhub.stockservice.stock.application.event.external.StockDecreaseSuccessEvent;
import com.athenhub.stockservice.stock.application.service.StockDecreaseSuccessEventPublisher;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock.RabbitStockProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 재고 감소 성공 이벤트를 RabbitMQ로 발행하는 구현체이다.
 *
 * <p>본 클래스는 {@link StockDecreaseSuccessEventPublisher} 인터페이스를 구현하며, 재고 감소 비즈니스 로직이 정상 처리된 이후 외부
 * 시스템(예: Order 서비스, Notification 서비스 등)에 성공 이벤트를 전달하는 역할을 맡는다.
 *
 * <p>Exchange 및 RoutingKey 설정은 {@link RabbitStockProperties}로부터 주입받아 사용하며, 메시지 전송은 Spring AMQP의
 * {@link RabbitTemplate}을 통해 수행된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockDecreasedEventPublisher implements StockDecreaseSuccessEventPublisher {

  /** RabbitMQ 메시지 전송을 위한 템플릿. */
  private final RabbitTemplate rabbitTemplate;

  /** Stock 서비스의 RabbitMQ 설정 정보. */
  private final RabbitStockProperties stockProperties;

  /**
   * 재고 감소 성공 이벤트를 RabbitMQ로 발행한다.
   *
   * <p>재고 감소 처리가 정상적으로 완료된 경우 호출되며,<br>
   * {@code rabbit.stock.decrease-success.routing-key} 에 해당하는 RoutingKey를 사용하여 성공 이벤트를 외부로 전송한다.
   *
   * <p>메시지는 재고 서비스의 기본 Exchange({@code rabbit.stock.exchange})로 발행되며, 사용하는 Queue 및 RoutingKey 구성은
   * {@link RabbitStockProperties#getDecreaseSuccess()} 에서 관리한다.
   *
   * @param event 재고 감소 성공 이벤트 데이터 (주문 ID, 상품/옵션 ID, 감소 수량 등의 정보를 포함)
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public void publish(StockDecreaseSuccessEvent event) {
    rabbitTemplate.convertAndSend(
        stockProperties.getExchange(), stockProperties.getDecreaseSuccess().getRoutingKey(), event);
  }
}
