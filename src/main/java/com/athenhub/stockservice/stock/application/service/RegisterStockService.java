package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.dto.RegisterRequest;
import com.athenhub.stockservice.stock.domain.service.BelongsToValidator;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterStockService {

  private final ProductAccessPermissionValidator permissionValidator;
  private final BelongsToValidator belongsToValidator;

  public RegisterResponse register(RegisterRequest request, AccessContext accessContext) {
    Stock stock = Stock.of(request, accessContext, belongsToValidator, permissionValidator);
    // TODO: 이벤트 멀티 캐스팅(내부, 외부 모두 사용)
    return new RegisterResponse(stock.getId().toUuid());
  }
}
