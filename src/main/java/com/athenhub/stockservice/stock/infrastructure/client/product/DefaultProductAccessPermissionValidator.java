package com.athenhub.stockservice.stock.infrastructure.client.product;

import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionValidator;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultProductAccessPermissionValidator implements ProductAccessPermissionValidator {

  private final ProductClient productClient;

  public boolean canAccess(AccessContext accessContext, UUID productId) {
    ProductDetail product = productClient.getProduct(productId);
    return isProductInSameContext(accessContext, product);
  }

  private static boolean isProductInSameContext(
      AccessContext accessContext, ProductDetail product) {
    return Objects.equals(accessContext.hubId(), product.hubId())
        || Objects.equals(accessContext.vendorId(), product.vendorId());
  }
}
