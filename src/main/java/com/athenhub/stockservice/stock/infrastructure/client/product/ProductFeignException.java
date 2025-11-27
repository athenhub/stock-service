package com.athenhub.stockservice.stock.infrastructure.client.product;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

/**
 * Product 서비스 연동 과정에서 발생하는 예외를 나타낸다.
 *
 * <p>Feign Client를 통해 Product 서비스 호출 중 발생한 오류를 재고 서비스 애플리케이션 계층에서 처리 가능한 예외 형태로 변환하기 위해 사용된다.
 *
 * <p>주로 {@code ProductFeignErrorDecoder}에서 생성되어 상위 계층으로 전달된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public class ProductFeignException extends AbstractServiceException {

  /**
   * Product 서비스 호출 중 발생한 예외를 생성한다.
   *
   * @param errorCode 에러 유형을 나타내는 코드
   * @param errorArgs 메시지 포맷팅을 위한 가변 인자
   * @author 김지원
   * @since 1.0.0
   */
  public ProductFeignException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
