package com.athenhub.stockservice.product.domain.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

public class StockDomainException extends AbstractServiceException {

  public StockDomainException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
