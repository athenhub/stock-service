package com.athenhub.stockservice.stock.domain.repository;

import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.vo.OrderId;
import com.athenhub.stockservice.stock.domain.vo.StockHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 재고 이력(StockHistory)에 대한 영속성 처리를 담당하는 Repository이다.
 *
 * <p>재고 생성, 증가, 감소 등과 같이 재고 상태 변경이 발생할 때마다 해당 이력을 저장하고 조회하기 위해 사용된다.
 *
 * <p>기본 CRUD 기능은 {@link JpaRepository}를 통해 제공되며, 필요 시 도메인 요구사항에 맞는 조회 메서드를 추가할 수 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface StockHistoryRepository extends JpaRepository<StockHistory, StockHistoryId> {

  /**
   * 특정 주문에 대한 재고 이력이 이미 존재하는지 확인한다.
   *
   * <p>주로 중복 주문 처리, 멱등성(Idempotency) 보장을 위해 동일한 주문에 대해 재고 감소가 한 번만 처리되었는지를 검증할 때 사용된다.
   *
   * @param orderId 주문 ID
   * @return 재고 이력이 이미 존재하면 {@code true}, 존재하지 않으면 {@code false}
   * @author 김지원
   * @since 1.0.0
   */
  boolean existsByOrderId(OrderId orderId);
}
