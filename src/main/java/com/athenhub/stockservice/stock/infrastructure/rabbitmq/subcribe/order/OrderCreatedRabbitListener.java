package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.order;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.athenhub.stockservice.stock.application.dto.StockDecreaseRequest;
import com.athenhub.stockservice.stock.infrastructure.rabbitmq.config.stock.RabbitStockProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 주문 서비스(Order Service)에서 발행된 {@link OrderCreatedEvent}를 수신하는 RabbitMQ 리스너이다.
 *
 * <p>본 리스너의 책임은 단일하며 다음과 같다:
 *
 * <ul>
 *   <li>OrderCreatedEvent를 수신한다.
 *   <li>상품 옵션별 재고 감소 요청을 {@link StockDecreaseRequest} 형태로 변환한다.
 *   <li>변환된 요청 목록을 하나의 {@link StockDecreaseBatchEvent}로 묶는다.
 *   <li>재고 감소 처리를 담당하는 내부 큐(<b>stock.decreased.queue</b>)로 이벤트를 다시 발행한다.
 * </ul>
 *
 * <p>즉, 이 클래스는 “주문 이벤트 → 재고 감소 입력 이벤트”로 변환하는 Adapter 역할을 담당하며, 실제 재고 감소 비즈니스 로직은 수행하지 않는다.
 *
 * <p><b>디자인 의도</b>
 *
 * <ul>
 *   <li>주문 이벤트와 재고 감소 비즈니스 로직을 명확히 분리하기 위함.
 *   <li>Retry, DLQ, 멱등성 등은 재고 서비스 내부 Consumer가 처리하도록 책임을 분리함.
 *   <li>옵션(Variant) 단위 요청을 하나의 Batch 이벤트로 묶어 재고 감소 처리의 일관성 유지.
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class OrderCreatedRabbitListener {

  /** 재고 감소 이벤트를 내부 Stock Exchange로 발행하기 위한 RabbitTemplate. */
  private final RabbitTemplate rabbitTemplate;

  /** 재고 관련 RabbitMQ 설정 정보를 담고 있는 Properties. */
  private final RabbitStockProperties stockProperties;

  /**
   * 주문 생성 이벤트를 수신하여 재고 감소 Batch 이벤트로 변환하고 내부 큐로 재발행한다.
   *
   * <p><b>처리 순서</b>
   *
   * <ol>
   *   <li>OrderCreatedEvent 수신
   *   <li>각 상품 옵션 정보를 StockDecreaseRequest로 변환
   *   <li>모든 요청을 하나의 StockDecreaseBatchEvent로 묶음
   *   <li>재고 감소 처리를 담당하는 stock.decreased.queue로 이벤트 발행
   * </ol>
   *
   * <p>이 단계에서는 재고 감소 비즈니스 로직을 수행하지 않으며, 메시지 전달에만 집중한다.
   *
   * @param event 주문 생성 이벤트
   */
  @RabbitListener(queues = "${rabbit.order.created.queue}")
  public void listen(OrderCreatedEvent event) {

    // 1. 주문에 포함된 각 상품 옵션을 재고 감소 요청 DTO로 변환한다.
    List<StockDecreaseRequest> stockDecreaseRequests =
        event.products().stream()
            .map(
                product ->
                    new StockDecreaseRequest(
                        product.productId(),
                        product.variantId(),
                        product.quantity(),
                        event.orderedAt()))
            .toList();

    // 2. 주문 단위로 묶인 재고 감소 Batch 이벤트 생성.
    StockDecreaseBatchEvent batchEvent =
        new StockDecreaseBatchEvent(event.orderId(), event.orderedAt(), stockDecreaseRequests);

    // 3. 내부 재고 감소 큐로 Batch 이벤트 발행.
    //    이후 처리(재고 감소, Retry, DLQ)는 별도의 Consumer가 담당한다.
    rabbitTemplate.convertAndSend(
        stockProperties.getExchange(), stockProperties.getDecrease().getRoutingKey(), batchEvent);
  }
}
