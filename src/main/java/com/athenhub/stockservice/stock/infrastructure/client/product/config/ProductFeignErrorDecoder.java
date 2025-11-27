package com.athenhub.stockservice.stock.infrastructure.client.product.config;

import com.athenhub.commoncore.error.GlobalErrorCode;
import com.athenhub.stockservice.stock.infrastructure.client.product.ProductFeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * 상품(Product) 서비스 호출 시 발생하는 Feign 예외를 재고(Stock) 서비스에서 사용하는 {@link ProductFeignException}으로 변환한다.
 *
 * <p>외부(Product) 서비스의 HTTP 응답 상태 코드를 기준으로 내부 공통 에러 코드({@link GlobalErrorCode})와 메시지를 매핑한다.
 *
 * <p>- 목적: 외부 서비스 예외를 내부 도메인/애플리케이션 예외로 추상화 <br>
 * - 효과: 서비스 간 결합도 감소 + 에러 처리 일관성 보장
 *
 * @author 김지원
 * @since 1.0.0
 */
public class ProductFeignErrorDecoder implements ErrorDecoder {

  /**
   * Feign 클라이언트 호출 결과로 반환된 HTTP 상태 코드를 분석하여 Stock 도메인에서 정의한 예외로 변환한다.
   *
   * @param methodKey Feign 메서드 식별 키 (클라이언트명 + 메서드 시그니처)
   * @param response Feign 응답 객체 (상태 코드, 헤더, 바디 포함)
   * @return 상태 코드에 맞춰 생성된 {@link ProductFeignException}
   */
  @Override
  public Exception decode(String methodKey, Response response) {

    return switch (response.status()) {

        // 잘못된 요청 (클라이언트 요청 자체가 올바르지 않은 경우)
      case 400 -> new ProductFeignException(GlobalErrorCode.BAD_REQUEST, "요청 형식이 올바르지 않습니다.");

        // 요청한 상품이 존재하지 않는 경우
      case 404 -> new ProductFeignException(GlobalErrorCode.NOT_FOUND, "요청한 상품을 찾을 수 없습니다.");

        // 인증 실패 또는 권한 부족
      case 401, 403 ->
          new ProductFeignException(GlobalErrorCode.FORBIDDEN, "해당 상품에 대한 접근 권한이 없습니다.");

        // 상품 서비스 내부 예외 발생
      case 500 ->
          new ProductFeignException(GlobalErrorCode.INTERNAL_SERVER_ERROR, "상품 서비스에서 오류가 발생했습니다.");

        // 그 외 알 수 없는 에러 (네트워크 이슈, 예상하지 못한 상태 코드 등)
      default ->
          new ProductFeignException(
              GlobalErrorCode.INTERNAL_SERVER_ERROR, "상품 서비스와의 통신 중 오류가 발생했습니다.");
    };
  }
}
