package com.athenhub.stockservice.stock.infrastructure.rabbitmq.subcribe.order;

import com.athenhub.stockservice.stock.application.service.StockDecreaseHandler;
import com.athenhub.stockservice.stock.domain.dto.StockDecreaseRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 주문 생성 이벤트를 수신하여 재고 감소를 트리거하는 RabbitMQ 리스너이다.
 *
 * <p>주문 서비스로부터 전달받은 {@link OrderCreatedEvent}를 기반으로 재고 감소 요청 목록({@link StockDecreaseRequest})을
 * 생성하고, {@link StockDecreaseHandler}에 위임하여 주문 단위로 재고 감소를 처리한다.
 *
 * <p><b>처리 흐름</b>
 *
 * <ol>
 *   <li>RabbitMQ Queue에서 {@code OrderCreatedEvent} 수신
 *   <li>이벤트 내 상품 목록을 {@code StockDecreaseRequest}로 변환
 *   <li>주문 ID와 함께 {@link StockDecreaseHandler#decreaseAll} 호출
 * </ol>
 *
 * <p><b>특징</b>
 *
 * <ul>
 *   <li>옵션(Variant) 단위가 아닌 <b>주문(Order) 단위</b>로 재고 감소를 처리한다.
 *   <li>실제 비즈니스 로직은 {@link StockDecreaseHandler}에 위임하여 역할을 분리한다.
 *   <li>메시지 수신 책임만 가지며, 트랜잭션은 Handler 내부에서 관리된다.
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class OrderCreatedRabbitListener {

  /** 재고 감소 비즈니스 로직을 담당하는 핸들러. */
  private final StockDecreaseHandler stockDecreaseHandler;

  /**
   * 주문 생성 이벤트를 수신하여 재고 감소 요청을 생성하고 처리한다.
   *
   * @param event 주문 생성 이벤트
   */
  @RabbitListener(queues = "${rabbit.order.created.queue}")
  public void listen(OrderCreatedEvent event) {

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

    stockDecreaseHandler.decreaseAll(event.orderId(), stockDecreaseRequests);
  }
}
