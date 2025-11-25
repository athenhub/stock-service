package com.athenhub.stockservice.stock.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.application.dto.StockInitializeCommand;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

/**
 * RegisterStockService 통합 테스트.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Transactional
@SpringBootTest
@RecordApplicationEvents
class RegisterStockServiceIntegrationTest {

  @Autowired private RegisterStockService registerStockService;
  @Autowired private StockRepository stockRepository;
  @Autowired private StockHistoryRepository stockHistoryRepository;

  @Test
  @DisplayName("재고 등록 시 Stock 저장, StockHistory 생성, 이벤트 발행이 모두 수행된다.")
  void register_success() {
    // given
    StockInitializeCommand command = StockInitializeCommandFixture.create();
    ProductId productId = ProductId.of(command.productId());

    // when
    RegisterResponse response = registerStockService.register(command);

    // then 1. Stock 저장 확인
    List<Stock> stocks = stockRepository.findByProductId(productId);
    assertThat(stocks).hasSize(command.productVariants().size());

    // then 2. StockHistory 생성 확인
    assertThat(stockHistoryRepository.findAll()).hasSize(command.productVariants().size());

    // then 3. 반환값 검증
    assertThat(response.productId()).isEqualTo(command.productId());

    verify(stockRegisteredEventPublisher, times(1)).publish(any());
  }
}
