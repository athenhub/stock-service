package com.athenhub.stockservice.stock.application.service;

import static com.athenhub.stockservice.stock.application.exception.ApplicationErrorCode.REGISTER_NOT_ALLOWED;

import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.dto.ProductVariantMatchCommand;
import com.athenhub.stockservice.stock.domain.service.BelongsToValidator;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionValidator;
import com.athenhub.stockservice.stock.domain.service.ProductVariantConsistencyValidator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 재고 등록을 위한 퍼사드(Facade) 서비스이다.
 *
 * <p>재고 등록 전 필요한 모든 선행 조건(소속 검증, 접근 권한 검증, 상품 옵션 일관성 검증)을 검증한 뒤, 실제 재고 등록 로직을 {@link
 * RegisterStockService}에 위임한다.
 *
 * <p>애플리케이션 레벨에서 여러 도메인/인프라 규칙을 조합하여 하나의 유스케이스(재고 등록)를 구성하는 역할을 가진다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class StockRegistrationFacade {

  private final ProductAccessPermissionValidator permissionValidator;
  private final BelongsToValidator belongsToValidator;
  private final ProductVariantConsistencyValidator productVariantConsistencyValidator;
  public final RegisterStockService registerStockService;

  /**
   * 재고 등록을 수행한다.
   *
   * <p>다음 검증 단계를 모두 통과한 경우에만 실제 재고 등록을 수행한다.
   *
   * <ul>
   *   <li>사용자의 소속(Hub/Vendor) 유효성 검증
   *   <li>상품 접근(재고 등록) 권한 검증
   *   <li>상품 옵션과 재고 등록 요청 옵션의 일관성 검증
   * </ul>
   *
   * @param accessContext 접근 주체 및 소속 정보
   * @param command 재고 초기화 요청 정보
   * @return 등록된 상품 ID 응답
   * @throws StockApplicationException 검증 실패 시 발생
   * @author 김지원
   * @since 1.0.0
   */
  public RegisterResponse register(AccessContext accessContext, StockInitializeCommand command) {
    validateBelongToOrganization(accessContext);
    validateProductAccessPermission(accessContext, command);
    validateAllProductVariantExists(command);

    return registerStockService.register(command);
  }

  /**
   * 사용자가 해당 조직(Hub/Vendor)에 소속되어 있는지 검증한다.
   *
   * @param accessContext 접근 주체 및 소속 정보
   * @throws StockApplicationException 소속되지 않은 경우 발생
   * @author 김지원
   * @since 1.0.0
   */
  private void validateBelongToOrganization(AccessContext accessContext) {
    if (!belongsToValidator.belongsTo(accessContext)) {
      throw new StockApplicationException(REGISTER_NOT_ALLOWED, "현재 사용자는 해당 허브/벤더에 소속되어 있지 않습니다.");
    }
  }

  /**
   * 상품에 대한 재고 등록 권한이 있는지 검증한다.
   *
   * @param accessContext 접근 주체 및 소속 정보
   * @param command 재고 초기화 요청 정보
   * @throws StockApplicationException 접근 권한이 없는 경우 발생
   * @author 김지원
   * @since 1.0.0
   */
  private void validateProductAccessPermission(
      AccessContext accessContext, StockInitializeCommand command) {

    if (!permissionValidator.canAccess(accessContext, command.productId())) {
      throw new StockApplicationException(REGISTER_NOT_ALLOWED, "해당 상품에 대한 재고 등록 권한이 없습니다.");
    }
  }

  /**
   * 재고 등록 요청에 포함된 모든 상품 옵션이 실제 상품에 존재하는지 검증한다.
   *
   * @param command 재고 초기화 요청 정보
   * @throws StockApplicationException 옵션 목록이 일치하지 않는 경우 발생
   * @author 김지원
   * @since 1.0.0
   */
  private void validateAllProductVariantExists(StockInitializeCommand command) {
    ProductVariantMatchCommand matchCommand = convertToProductVariantMatchCommand(command);

    if (!productVariantConsistencyValidator.matches(matchCommand)) {
      throw new StockApplicationException(
          REGISTER_NOT_ALLOWED, "상품에 등록된 옵션 목록과 재고 등록 요청 옵션 목록이 일치하지 않습니다.");
    }
  }

  /**
   * {@link StockInitializeCommand}를 {@link ProductVariantMatchCommand}로 변환한다.
   *
   * <p>상품 ID와 요청된 옵션 ID 목록을 추출하여 옵션 일관성 검증에 사용할 커맨드를 생성한다.
   *
   * @param command 재고 초기화 요청 정보
   * @return 옵션 비교를 위한 커맨드
   * @author 김지원
   * @since 1.0.0
   */
  private static ProductVariantMatchCommand convertToProductVariantMatchCommand(
      StockInitializeCommand command) {

    UUID productId = command.productId();
    List<UUID> variantIds =
        command.productVariants().stream().map(StockInitializeCommand.ProductVariant::id).toList();

    return new ProductVariantMatchCommand(productId, variantIds);
  }
}
