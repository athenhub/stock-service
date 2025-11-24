package com.athenhub.stockservice.product.domain.service;

import com.athenhub.stockservice.product.domain.dto.AccessContext;

import java.util.UUID;

public interface ProductAccessPermissionChecker {
  boolean canAccess(AccessContext accessContext, UUID productId);
}
