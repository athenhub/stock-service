package com.athenhub.stockservice.stock.application.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

/**
 * 재고(Stock) 도메인의 애플리케이션 계층에서 발생하는 예외를 나타낸다.
 *
 * <p>비즈니스 로직 수행 중 발생한 오류를 공통 에러 코드({@link ErrorCode})와 함께 전달하기 위해 사용되며, 글로벌 예외 처리기에서 일관된 오류 응답을
 * 생성하는 데 활용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public class StockApplicationException extends AbstractServiceException {

  /**
   * 재고 애플리케이션 예외를 생성한다.
   *
   * @param errorCode 에러 유형을 나타내는 코드
   * @param errorArgs 메시지 포맷팅을 위한 가변 인자
   * @author 김지원
   * @since 1.0.0
   */
  public StockApplicationException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
