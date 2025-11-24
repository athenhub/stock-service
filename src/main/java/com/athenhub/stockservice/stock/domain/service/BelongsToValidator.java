package com.athenhub.stockservice.stock.domain.service;

import com.athenhub.stockservice.stock.domain.dto.AccessContext;

public interface BelongsToValidator {

  /** 사용자가 주어진 컨텍스트의 허브/업체에 소속되어 있는지 확인한다. */
  boolean belongsTo(AccessContext context);
}
