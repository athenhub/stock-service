package com.athenhub.stockservice.stock.infrastructure.client.member;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

public class MemberFeignException extends AbstractServiceException {

  public MemberFeignException(ErrorCode errorCode,
      Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
