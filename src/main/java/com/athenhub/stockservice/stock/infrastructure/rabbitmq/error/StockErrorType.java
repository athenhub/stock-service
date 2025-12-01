package com.athenhub.stockservice.stock.infrastructure.rabbitmq.error;

/**
 * 재고 감소 처리 과정에서 발생할 수 있는 오류 유형을 정의한 Enum이다.
 *
 * <p>RabbitMQ Retry/DLQ 전략에서 메시지 헤더에 오류 유형을 기록하여, 이후 보상 트랜잭션 처리(주문 취소, 결제 취소 등) 또는 장애 분석 시 사용된다.
 *
 * <p>오류의 성격에 따라 비즈니스 실패(OUT_OF_STOCK), Retry 정책 소진(RETRY_EXCEEDED), 예상치 못한 시스템 오류(UNKNOWN_ERROR)로
 * 구분한다.
 *
 * <p>메시지 발행 시 {@code x-error-type} 헤더에 본 Enum의 이름을 기록하고, Consumer 측에서는 이를 기반으로 도메인 이벤트 생성 또는 보상 로직을
 * 수행한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum StockErrorType {

  /**
   * 재고 부족 등 비즈니스 규칙을 위반하여 재고 감소가 실패한 경우.
   *
   * <p>예:
   *
   * <ul>
   *   <li>요청된 수량이 현재 재고보다 많음
   *   <li>이미 완전 소진된 재고
   * </ul>
   *
   * <p>재시도를 통해 해결될 수 없는 실패로, 즉시 실패 이벤트를 발행해야 한다.
   */
  OUT_OF_STOCK,

  /**
   * RabbitMQ Retry 횟수를 초과하여 더 이상 재시도가 불가능한 경우.
   *
   * <p>예:
   *
   * <ul>
   *   <li>Optimistic Lock 충돌이 지속적으로 발생하는 경우
   *   <li>임시 장애가 Retry 내에 회복되지 못한 경우
   * </ul>
   *
   * <p>DLQ로 이동하여 보상 트랜잭션 처리가 필요하다.
   */
  RETRY_EXCEEDED,

  /**
   * 예상치 못한 모든 시스템 오류.
   *
   * <p>예:
   *
   * <ul>
   *   <li>서버 내부 NullPointerException
   *   <li>메시지 역직렬화 실패
   *   <li>인프라 연결 오류
   * </ul>
   *
   * <p>정확한 원인을 파악하기 위해 오류 로그 분석이 필요하며, DLQ 이동 후 보상/모니터링 처리 대상이 된다.
   */
  UNKNOWN_ERROR
}
