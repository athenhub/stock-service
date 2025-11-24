package com.athenhub.stockservice.product.domain.exception;

import com.athenhub.commoncore.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PermissionErrorCode implements ErrorCode {
  REGISTER_NOT_ALLOWED(HttpStatus.FORBIDDEN.value(), "REGISTER_NOT_ALLOWED");

  private final int status;
  private final String code;

  @Override
  public int getStatus() {
    return 0;
  }

  @Override
  public String getCode() {
    return "";
  }
}
