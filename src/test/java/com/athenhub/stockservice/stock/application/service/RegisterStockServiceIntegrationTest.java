package com.athenhub.stockservice.stock.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand.ProductVariant;
import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * RegisterStockService 통합 테스트.
 *
 * @author 김지원
 * @since 1.0.0.
 */
@SpringBootTest
@Transactional
class RegisterStockServiceIntegrationTest {

  @Autowired private RegisterStockService registerStockService;

  @Autowired private StockRepository stockRepository;

  @Autowired private StockHistoryRepository stockHistoryRepository;

  /** 이벤트 발행자만 Mock */
  @MockitoBean private StockRegisteredEventPublisher stockRegisteredEventPublisher;

  @Test
  @DisplayName("재고 등록 시 Stock, StockHistory가 저장되고 이벤트가 발행된다.")
  void register_success() {
    // given
    UUID productId = UUID.randomUUID();

    StockInitializeCommand command =
        new StockInitializeCommand(
            productId,
            List.of(
                new ProductVariant(UUID.randomUUID(), 10),
                new ProductVariant(UUID.randomUUID(), 20)));

    // when
    RegisterResponse response = registerStockService.register(command);

    // then
    // 1. 응답 검증
    assertThat(response.productId()).isEqualTo(productId);

    // 2. Stock 저장 확인
    List<Stock> stocks = stockRepository.findAll();
    assertThat(stocks).hasSize(2);

    // 3. StockHistory 저장 확인
    List<StockHistory> histories = stockHistoryRepository.findAll();
    assertThat(histories).hasSize(2);

    // 4. 이벤트 발행 확인
    verify(stockRegisteredEventPublisher, times(1)).publish(any());
  }

  @Test
  @DisplayName("중복된 variantId가 있으면 예외가 발생한다.")
  void register_duplicatedVariantId_fail() {
    // given
    UUID productId = UUID.randomUUID();
    UUID variantId = UUID.randomUUID();

    StockInitializeCommand command =
        new StockInitializeCommand(
            productId,
            List.of(new ProductVariant(variantId, 10), new ProductVariant(variantId, 20)));

    // when & then
    assertThatThrownBy(() -> registerStockService.register(command))
        .isInstanceOf(StockApplicationException.class);

    // 이벤트가 발행되지 않았는지 확인
    verify(stockRegisteredEventPublisher, times(0)).publish(any());
  }
}
