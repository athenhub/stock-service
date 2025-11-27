package com.athenhub.stockservice.stock.application.exception;

import com.athenhub.commoncore.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 재고 도메인에서 발생하는 권한 관련 에러 코드를 정의한다.
 *
 * <p>재고 등록, 수정, 삭제 등 특정 행위에 대해 사용자에게 권한이 없을 경우 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequiredArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {

  /** 재고 등록 권한이 없는 경우 발생하는 에러. */
  REGISTER_NOT_ALLOWED(HttpStatus.FORBIDDEN.value(), "REGISTER_NOT_ALLOWED"),
  DUPLICATED_VARIANT(HttpStatus.BAD_REQUEST.value(), "DUPLICATED_VARIANT"),
  STOCK_DECREASE_CONFLICT(HttpStatus.CONFLICT.value(), "STOCK_DECREASE_CONFLICT");
  private final int status;
  private final String code;

  /**
   * HTTP 상태 코드를 반환한다.
   *
   * @return HTTP Status
   */
  @Override
  public int getStatus() {
    return status;
  }

  /**
   * 에러 코드를 반환한다.
   *
   * @return 에러 코드 문자열
   */
  @Override
  public String getCode() {
    return code;
  }
}
