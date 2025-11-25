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

@Service
@RequiredArgsConstructor
public class StockRegistrationFacade {

  private final ProductAccessPermissionValidator permissionValidator;
  private final BelongsToValidator belongsToValidator;
  private final ProductVariantConsistencyValidator productVariantConsistencyValidator;

  public final RegisterStockService registerStockService;

  public RegisterResponse register(AccessContext accessContext, StockInitializeCommand command) {
    validateBelongToOrganization(accessContext);
    validateProductAccessPermission(accessContext, command);
    validateAllProductVariantExists(command);

    return registerStockService.register(command);
  }

  private void validateBelongToOrganization(AccessContext accessContext) {
    if (!belongsToValidator.belongsTo(accessContext)) {
      throw new StockApplicationException(REGISTER_NOT_ALLOWED, "현재 사용자는 해당 허브/벤더에 소속되어 있지 않습니다.");
    }
  }

  private void validateProductAccessPermission(
      AccessContext accessContext, StockInitializeCommand command) {
    if (!permissionValidator.canAccess(accessContext, command.productId())) {
      throw new StockApplicationException(REGISTER_NOT_ALLOWED, "해당 상품에 대한 재고 등록 권한이 없습니다.");
    }
  }

  private void validateAllProductVariantExists(StockInitializeCommand command) {
    ProductVariantMatchCommand matchCommand = convertToProductVariantMatchCommand(command);
    if (!productVariantConsistencyValidator.matches(matchCommand)) {
      throw new StockApplicationException(
          REGISTER_NOT_ALLOWED, "상품에 등록된 옵션 목록과 재고 등록 요청 옵션 목록이 일치하지 않습니다.");
    }
  }

  private static ProductVariantMatchCommand convertToProductVariantMatchCommand(
      StockInitializeCommand command) {
    UUID productId = command.productId();
    List<UUID> variantIds =
        command.productVariants().stream().map(StockInitializeCommand.ProductVariant::id).toList();
    return new ProductVariantMatchCommand(productId, variantIds);
  }
}
