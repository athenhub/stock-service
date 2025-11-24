package com.athenhub.stockservice.stock.domain.service;

import com.athenhub.stockservice.stock.domain.dto.AccessContext;

import java.util.UUID;

public interface ProductAccessPermissionChecker {
  boolean canAccess(AccessContext accessContext, UUID productId);
}
