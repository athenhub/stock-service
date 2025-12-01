package com.athenhub.stockservice.stock.domain.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;
import com.athenhub.stockservice.stock.application.exception.ApplicationErrorCode;

/**
 * 재고(Stock) 도메인에서 재고 부족 상황을 나타내기 위해 사용되는 예외이다.
 *
 * <p>요청된 수량만큼 재고를 차감할 수 없는 경우(예: 재고 부족, 이미 소진된 상품 등) 발생하며, 도메인 규칙(“재고는 0개 미만이 될 수 없다”)을 위반한 상황을
 * 표현한다.
 *
 * <p>이 예외는 도메인 계층에서 발생하여 애플리케이션 계층, 컨트롤러 계층까지 전파되며, {@link ApplicationErrorCode} 또는 재고 도메인에서 정의된
 * {@link ErrorCode} 와 함께 사용된다.
 *
 * <p>예외 메시지는 메시지 해석기(MessageResolver)를 통해 로컬라이징된 사용자 메시지로 변환될 수 있으며, 로그 및 알림 시스템에서도 문제 원인 파악을 위해
 * 활용된다.
 *
 * <h3>주요 발생 시나리오</h3>
 *
 * <ul>
 *   <li>요청된 감소 수량이 현재 재고보다 많은 경우
 *   <li>이미 판매 완료되어 재고가 0인 경우
 *   <li>도메인의 재고 감소 정책(미리 정의된 최소 수량 등)에 반하는 경우
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public class InsufficientStockException extends AbstractServiceException {

  /**
   * 지정된 에러 코드를 기반으로 재고 부족 예외를 생성한다.
   *
   * @param errorCode 재고 도메인에서 정의한 에러 코드
   * @param errorArgs 메시지 변환 시 사용될 동적 인자
   */
  public InsufficientStockException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }

  /**
   * 지정된 에러 코드와 커스텀 메시지를 기반으로 재고 부족 예외를 생성한다.
   *
   * @param errorCode 재고 도메인에서 정의한 에러 코드
   * @param message 예외에 포함될 커스텀 메시지
   * @param errorArgs 메시지 변환 시 사용될 동적 인자
   */
  public InsufficientStockException(ErrorCode errorCode, String message, Object... errorArgs) {
    super(errorCode, message, errorArgs);
  }
}
