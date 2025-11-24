package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.global.infrastructure.springevent.Events;
import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.dto.RegisterRequest;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.service.BelongsToValidator;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 재고(Stock) 등록을 담당하는 애플리케이션 서비스이다.
 *
 * <p>외부(Controller 등)로부터 재고 등록 요청을 전달받아 다음 책임을 수행한다.
 *
 * <ul>
 *   <li>사용자 소속 여부 검증 (BelongsToValidator)
 *   <li>상품 접근 권한 검증 (ProductAccessPermissionValidator)
 *   <li>재고 엔티티 생성 및 저장
 *   <li>재고 생성 이벤트 발행 (StockCreatedEvent)
 * </ul>
 *
 * <p>이벤트는 {@link Events} 유틸을 통해 발행되며, 내부적으로 Spring EventListener 또는 외부 메시징(RabbitMQ 등)과 연계될 수 있다.
 *
 * <p>트랜잭션 범위 내에서 재고 생성과 이벤트 발행이 함께 처리된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RegisterStockService {

  /** 상품에 대한 접근 권한을 검증하는 도메인 서비스. */
  private final ProductAccessPermissionValidator permissionValidator;

  /** 사용자의 소속(허브/업체)을 검증하는 도메인 서비스. */
  private final BelongsToValidator belongsToValidator;

  /** 재고(Stock) 영속화를 위한 레포지토리. */
  private final StockRepository stockRepository;

  /**
   * 재고를 등록하고, 재고 생성 이벤트를 발행한다.
   *
   * <p>권한 및 소속 검증은 {@link Stock} 생성 과정 내부에서 수행되며, 생성이 완료되면 {@link StockCreatedEvent}가 발행된다.
   *
   * @param request 재고 등록에 필요한 요청 데이터
   * @param accessContext 사용자 접근 정보(유저, 허브, 업체)
   * @return 생성된 재고의 식별자를 포함한 응답 객체
   */
  public RegisterResponse register(RegisterRequest request, AccessContext accessContext) {
    Stock stock = Stock.create(request, accessContext, belongsToValidator, permissionValidator);

    stockRepository.save(stock);

    // 내부 이벤트 발행 (Stock → History, 외부 메시지 등과 연결 가능)
    Events.trigger(StockCreatedEvent.from(stock));

    return new RegisterResponse(stock.getId().toUuid());
  }
}
