package com.athenhub.stockservice.stock.infrastructure.client.product;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

public class ProductFeignException extends AbstractServiceException {

  public ProductFeignException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
