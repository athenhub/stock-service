package com.athenhub.stockservice.stock.domain.service;

import com.athenhub.stockservice.stock.domain.dto.ProductVariantMatchCommand;

public interface ProductVariantConsistencyValidator {
  boolean matches(ProductVariantMatchCommand existenceCommand);
}
