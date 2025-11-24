package com.athenhub.stockservice.product.infrastructure.client;

import com.athenhub.stockservice.product.infrastructure.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/api/v1/products")
@FeignClient(name = "product-service", configuration = FeignClientConfig.class)
public interface ProductClient {
    @GetMapping("/{productId}")
    ProductDetail getProduct(UUID productId);
}
