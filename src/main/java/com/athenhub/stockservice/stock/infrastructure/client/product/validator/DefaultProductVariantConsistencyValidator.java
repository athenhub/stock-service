package com.athenhub.stockservice.stock.infrastructure.client.product.validator;

import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.dto.ProductVariantMatchCommand;
import com.athenhub.stockservice.stock.domain.service.ProductVariantConsistencyValidator;
import com.athenhub.stockservice.stock.infrastructure.client.product.ProductClient;
import com.athenhub.stockservice.stock.infrastructure.client.product.dto.ProductDetail;
import com.athenhub.stockservice.stock.infrastructure.client.product.dto.ProductVariantDetails;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Product 서비스와 통신하여 상품 및 옵션(Variant)의 존재 여부를 검증한다.
 *
 * <p>지정된 productId와 variantId 목록이 실제 Product 서비스에 존재하는지 확인하며, 하나라도 존재하지 않을 경우 예외를 발생시킨다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DefaultProductVariantConsistencyValidator
    implements ProductVariantConsistencyValidator {

  private final ProductClient productClient;

  /**
   * 상품 및 옵션이 모두 존재하는지 검증한다.
   *
   * <p>상품이 존재하지 않거나, 옵션 중 하나라도 존재하지 않을 경우 {@link StockApplicationException}이 발생한다.
   *
   * @param command 상품 및 옵션 식별자 정보
   * @return 모두 존재할 경우 true
   */
  @Override
  public boolean matches(ProductVariantMatchCommand command) {
    ProductDetail product = productClient.getProduct(command.productId());

    Set<UUID> existingVariantIds = extractExistingVariantIds(product);
    Set<UUID> requestedVariantIds = toRequestedVariantIdSet(command);

    return existingVariantIds.equals(requestedVariantIds);
  }

  private Set<UUID> extractExistingVariantIds(ProductDetail product) {
    return product.variants().stream()
        .map(ProductVariantDetails::variantId)
        .collect(Collectors.toSet());
  }

  private Set<UUID> toRequestedVariantIdSet(ProductVariantMatchCommand command) {
    return Set.copyOf(command.productVariantIds());
  }
}
