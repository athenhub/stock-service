package com.athenhub.stockservice.stock.application.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

public class StockApplicationException extends AbstractServiceException {

  public StockApplicationException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
