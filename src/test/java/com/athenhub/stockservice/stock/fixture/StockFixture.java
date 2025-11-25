package com.athenhub.stockservice.stock.fixture;

import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.dto.InitialStock;
import java.util.UUID;

/**
 * Stock 도메인 테스트를 위한 Fixture 클래스.
 *
 * <p>Stock 생성 시 필요한 AccessContext, RegisterRequest, 권한/소속 검증용 Stub 등을 테스트 상황에 맞게 쉽게 만들기 위한 유틸리티이다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public final class StockFixture {

  private StockFixture() {}

  /**
   * 기본 RegisterRequest를 생성한다.
   *
   * <p>수량은 기본값 10으로 고정되고, 상품/옵션 ID는 랜덤 UUID로 생성된다.
   */
  public static InitialStock defaultRegisterRequest() {
    return new InitialStock(UUID.randomUUID(), UUID.randomUUID(), 10);
  }

  /**
   * 지정한 수량을 가지는 RegisterRequest를 생성한다.
   *
   * @param quantity 재고 수량.
   */
  public static InitialStock registerRequest(int quantity) {
    return new InitialStock(UUID.randomUUID(), UUID.randomUUID(), quantity);
  }

  /**
   * 소속 O, 권한 O, 수량만 지정 가능한 Stock 을 생성한다.
   *
   * @param quantity 재고 수량.
   */
  public static Stock createWithQuantity(int quantity) {
    return Stock.create(registerRequest(quantity));
  }
}
