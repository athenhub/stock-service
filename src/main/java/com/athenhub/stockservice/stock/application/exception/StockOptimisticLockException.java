package com.athenhub.stockservice.stock.application.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

public class StockOptimisticLockException extends AbstractServiceException {

  public StockOptimisticLockException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
