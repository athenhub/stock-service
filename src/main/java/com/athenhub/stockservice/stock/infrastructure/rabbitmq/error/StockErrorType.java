package com.athenhub.stockservice.stock.infrastructure.rabbitmq.error;

public enum StockErrorType {
  OUT_OF_STOCK, // 재고 부족 (비즈니스 실패)
  RETRY_EXCEEDED, // retry 최댓값 초과
  UNKNOWN_ERROR // 예상치 못한 오류
}
