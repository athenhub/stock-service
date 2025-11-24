package com.athenhub.stockservice.stock.infrastructure.client;

import com.athenhub.stockservice.stock.infrastructure.config.FeignClientConfig;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/products")
@FeignClient(name = "product-service", configuration = FeignClientConfig.class)
public interface ProductClient {
  @GetMapping("/{productId}")
  ProductDetail getProduct(UUID productId);
}
