package com.athenhub.stockservice.stock.application.dto;

import java.util.UUID;

/**
 * 재고 등록 결과를 반환하기 위한 응답 DTO이다.
 *
 * <p>재고 등록이 성공적으로 완료되면, 생성된 Stock의 식별자(id)를 클라이언트에게 전달한다.
 *
 * <p>주요 사용 위치:
 *
 * <ul>
 *   <li>{@code RegisterStockService}의 반환값
 *   <li>Controller or Facade 계층의 응답 객체
 * </ul>
 *
 * @param stockId 생성된 재고(Stock)의 고유 식별자
 * @author 김지원
 * @since 1.0.0
 */
public record RegisterResponse(UUID stockId) {}
