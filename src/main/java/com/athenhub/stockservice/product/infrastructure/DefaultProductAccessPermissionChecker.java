package com.athenhub.stockservice.product.infrastructure;

import com.athenhub.stockservice.product.domain.dto.AccessContext;
import com.athenhub.stockservice.product.domain.service.ProductAccessPermissionChecker;
import com.athenhub.stockservice.product.infrastructure.client.ProductClient;
import com.athenhub.stockservice.product.infrastructure.client.ProductDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultProductAccessPermissionChecker implements ProductAccessPermissionChecker {


    private final ProductClient productClient;

    public boolean canAccess(AccessContext accessContext, UUID productId) {
        ProductDetail product = productClient.getProduct(productId);
        return isProductInSameContext(accessContext, product);
    }

    private static boolean isProductInSameContext(AccessContext accessContext, ProductDetail product) {
        return Objects.equals(accessContext.hubId(), product.hubId()) || Objects.equals(accessContext.vendorId(), product.vendorId());
    }
}
