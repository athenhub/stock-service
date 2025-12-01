package com.athenhub.stockservice.stock.application.service;

import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import com.athenhub.stockservice.stock.application.event.external.StockRegisteredEvent;
import com.athenhub.stockservice.stock.application.exception.ApplicationErrorCode;
import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.dto.InitialStock;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 재고를 초기 등록하는 애플리케이션 서비스이다.
 *
 * <p>상품에 속한 여러 옵션(Variant)에 대해 초기 재고를 생성하고, 재고 이력을 저장한 뒤 외부로 재고 등록 완료 이벤트를 발행한다.
 *
 * <p>주요 책임:
 *
 * <ul>
 *   <li>옵션 중복 검증
 *   <li>재고(Stock) 생성 및 저장
 *   <li>재고 이력(StockHistory) 기록
 *   <li>재고 등록 이벤트 발행
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RegisterStockService {

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;
  private final StockRegisteredEventPublisher stockRegisteredEventPublisher;

  /**
   * 재고를 초기 등록한다.
   *
   * <p>옵션(Variant) 중복 여부를 검증한 뒤 재고를 생성하고, 이력을 저장한 후 재고 등록 완료 이벤트를 발행한다.
   *
   * @param command 재고 초기화를 위한 커맨드
   * @return 등록된 상품 ID 응답
   * @throws StockApplicationException 옵션 ID가 중복된 경우
   * @author 김지원
   * @since 1.0.0
   */
  public RegisterResponse register(StockInitializeCommand command) {
    validateUniqueVariants(command);

    List<Stock> stocks = saveStocks(command);

    saveStockHistory(stocks);

    publishEvent(command.productId());
    return new RegisterResponse(command.productId());
  }

  /**
   * 옵션(Variant)의 중복 여부를 검증한다.
   *
   * <p>동일한 Variant ID가 2개 이상 포함되어 있는 경우 재고 초기화가 불가능하다고 판단하여 예외를 발생시킨다.
   *
   * @param command 재고 초기화를 위한 커맨드
   * @throws StockApplicationException 옵션 ID가 중복된 경우
   * @author 김지원
   * @since 1.0.0
   */
  private void validateUniqueVariants(StockInitializeCommand command) {
    long distinctCount =
        command.productVariants().stream()
            .map(StockInitializeCommand.ProductVariant::id)
            .distinct()
            .count();

    if (distinctCount != command.productVariants().size()) {
      throw new StockApplicationException(ApplicationErrorCode.DUPLICATED_VARIANT);
    }
  }

  /**
   * 커맨드 정보를 기반으로 재고를 생성하고 저장한다.
   *
   * @param command 재고 초기화를 위한 커맨드
   * @return 저장된 재고 목록
   * @author 김지원
   * @since 1.0.0
   */
  private List<Stock> saveStocks(StockInitializeCommand command) {
    List<Stock> stocks = convertToStocks(command);
    stockRepository.saveAll(stocks);
    return stocks;
  }

  /**
   * 커맨드를 재고 도메인 엔티티 목록으로 변환한다.
   *
   * <p>{@link InitialStock} DTO로 변환 후, 이를 {@link Stock} 엔티티로 생성한다.
   *
   * @param request 재고 초기화를 위한 커맨드
   * @return 생성된 재고 엔티티 목록
   * @author 김지원
   * @since 1.0.0
   */
  private List<Stock> convertToStocks(StockInitializeCommand request) {
    return request.productVariants().stream()
        .map(variant -> new InitialStock(request.productId(), variant.id(), variant.quantity()))
        .map(Stock::create)
        .toList();
  }

  /**
   * 재고 생성 이력을 저장한다.
   *
   * <p>각 재고에 대해 입고(INBOUND) 이력을 생성하여 {@link StockHistoryRepository}에 저장한다.
   *
   * @param stocks 생성된 재고 목록
   * @author 김지원
   * @since 1.0.0
   */
  private void saveStockHistory(List<Stock> stocks) {
    List<StockHistory> stockHistories =
        stocks.stream().map(stock -> StockHistory.inbound(stock, stock.getQuantity())).toList();

    stockHistoryRepository.saveAll(stockHistories);
  }

  /**
   * 재고 등록 완료 이벤트를 발행한다.
   *
   * <p>상품에 대한 모든 재고가 정상적으로 등록되었음을 외부 시스템(예: Product 서비스)에 알리기 위해 사용된다.
   *
   * @param productId 재고가 등록된 상품 ID
   * @author 김지원
   * @since 1.0.0
   */
  private void publishEvent(UUID productId) {
    stockRegisteredEventPublisher.publish(StockRegisteredEvent.from(productId));
  }
}
