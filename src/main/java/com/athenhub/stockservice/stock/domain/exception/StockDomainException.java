package com.athenhub.stockservice.stock.domain.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

/**
 * 재고(Stock) 도메인에서 발생하는 예외의 공통 클래스이다.
 *
 * <p>재고 생성, 수정, 권한 검증 등 도메인 규칙을 위반했을 때 {@link ErrorCode}를 기반으로 예외를 표현한다.
 *
 * <p>주로 {@link PermissionErrorCode} 등과 함께 사용되며, 도메인 계층에서 발생한 비즈니스 규칙 위반을 상위(애플리케이션 / 컨트롤러) 계층까지
 * 전달한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public class StockDomainException extends AbstractServiceException {

  /**
   * 지정된 에러 코드를 기반으로 재고 도메인 예외를 생성한다.
   *
   * @param errorCode 재고 도메인에서 정의한 에러 코드
   * @param errorArgs 에러 메시지에 전달할 추가 인자
   */
  public StockDomainException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
