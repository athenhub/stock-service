package com.athenhub.stockservice.stock.fixture;

import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.dto.RegisterRequest;
import com.athenhub.stockservice.stock.domain.service.BelongsToValidator;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionChecker;
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
   * 랜덤 UUID로 구성된 기본 AccessContext를 생성한다.
   *
   * <p>대부분의 테스트에서 공통으로 사용하는 기본 사용자 컨텍스트이다.
   */
  public static AccessContext defaultContext() {
    return new AccessContext(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
  }

  /**
   * 기본 RegisterRequest를 생성한다.
   *
   * <p>수량은 기본값 10으로 고정되고, 상품/옵션 ID는 랜덤 UUID로 생성된다.
   */
  public static RegisterRequest defaultRegisterRequest() {
    return new RegisterRequest(UUID.randomUUID(), UUID.randomUUID(), 10);
  }

  /**
   * 지정한 수량을 가지는 RegisterRequest를 생성한다.
   *
   * @param quantity 재고 수량.
   */
  public static RegisterRequest registerRequest(int quantity) {
    return new RegisterRequest(UUID.randomUUID(), UUID.randomUUID(), quantity);
  }

  /**
   * 항상 true를 반환하는 BelongsToValidator.
   *
   * <p>사용자가 항상 Hub/Vendor에 속해 있다고 가정하는 테스트에 사용한다.
   */
  public static BelongsToValidator alwaysBelongs() {
    return context -> true;
  }

  /**
   * 항상 false를 반환하는 BelongsToValidator.
   *
   * <p>사용자가 어떤 Hub/Vendor에도 속해 있지 않은 시나리오를 테스트할 때 사용한다.
   */
  public static BelongsToValidator neverBelongs() {
    return context -> false;
  }

  /**
   * 항상 true를 반환하는 ProductAccessPermissionChecker.
   *
   * <p>상품 접근 권한이 항상 허용된 상태를 가정한다.
   */
  public static ProductAccessPermissionChecker alwaysAllowed() {
    return (context, productId) -> true;
  }

  /**
   * 항상 false를 반환하는 ProductAccessPermissionChecker.
   *
   * <p>상품 접근 권한이 항상 거부된 상태를 가정한다.
   */
  public static ProductAccessPermissionChecker neverAllowed() {
    return (context, productId) -> false;
  }

  /**
   * 소속 O, 권한 O, 기본 수량(10)인 Stock 을 생성한다.
   *
   * <p>가장 일반적인 정상 시나리오(Happy Path) 테스트에 사용한다.
   */
  public static Stock create() {
    return Stock.of(defaultRegisterRequest(), defaultContext(), alwaysBelongs(), alwaysAllowed());
  }

  /**
   * 소속 O, 권한 O, 수량만 지정 가능한 Stock 을 생성한다.
   *
   * @param quantity 재고 수량.
   */
  public static Stock createWithQuantity(int quantity) {
    return Stock.of(registerRequest(quantity), defaultContext(), alwaysBelongs(), alwaysAllowed());
  }

  /**
   * 소속 X, 권한 O 인 사용자 시나리오용 Stock 을 생성한다.
   *
   * <p>BelongsToValidator만 실패하는 경우를 테스트할 때 사용한다.
   */
  public static Stock createNotBelongingUser() {
    return Stock.of(defaultRegisterRequest(), defaultContext(), neverBelongs(), alwaysAllowed());
  }

  /**
   * 소속 O, 권한 X 인 사용자 시나리오용 Stock 을 생성한다.
   *
   * <p>ProductAccessPermissionChecker만 실패하는 경우를 테스트할 때 사용한다.
   */
  public static Stock createNoPermission() {
    return Stock.of(defaultRegisterRequest(), defaultContext(), alwaysBelongs(), neverAllowed());
  }
}
