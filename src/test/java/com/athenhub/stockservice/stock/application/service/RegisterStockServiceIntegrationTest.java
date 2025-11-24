package com.athenhub.stockservice.stock.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.athenhub.stockservice.stock.application.dto.RegisterResponse;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.StockEventType;
import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.dto.AccessContext;
import com.athenhub.stockservice.stock.domain.dto.RegisterRequest;
import com.athenhub.stockservice.stock.domain.event.internal.StockCreatedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockHistoryRepository;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.service.BelongsToValidator;
import com.athenhub.stockservice.stock.domain.service.ProductAccessPermissionValidator;
import com.athenhub.stockservice.stock.domain.vo.StockId;
import com.athenhub.stockservice.stock.fixture.StockFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@RecordApplicationEvents
class RegisterStockServiceIntegrationTest {
  @Autowired RegisterStockService registerStockService;

  @Autowired StockRepository stockRepository;

  @Autowired StockHistoryRepository stockHistoryRepository;

  @MockitoBean BelongsToValidator belongsToValidator;

  @MockitoBean ProductAccessPermissionValidator permissionValidator;

  @Autowired ApplicationEvents applicationEvents;

  @Test
  @DisplayName("재고 등록 시 stock과 stockHistory가 함께 생성된다.")
  void register_success_with_spring() {
    // given
    RegisterRequest request = StockFixture.defaultRegisterRequest();
    AccessContext context = StockFixture.defaultContext();

    when(belongsToValidator.belongsTo(context)).thenReturn(true);
    when(permissionValidator.canAccess(context, request.productId())).thenReturn(true);

    // when
    RegisterResponse response = registerStockService.register(request, context);

    // then
    // Stock이 잘 저장되었는지 검증
    Stock stock = stockRepository.findById(StockId.of(response.stockId())).orElseThrow();
    assertThat(stock.getQuantity()).isEqualTo(10);
    assertThat(stock.getProductId()).isNotNull();

    // StockCreatedEvent가 발행되었는지 검증
    assertThat(applicationEvents.stream(StockCreatedEvent.class).count()).isEqualTo(1);

    // 내부 Spring Event에 의해 생성된 StockHistory 검증
    assertThat(stockHistoryRepository.findAll()).hasSize(1);

    StockHistory history = stockHistoryRepository.findAll().getFirst();

    assertThat(history.getProductId()).isEqualTo(stock.getProductId());
    assertThat(history.getChangedQuantity()).isEqualTo(stock.getQuantity());
    assertThat(history.getEventType()).isEqualTo(StockEventType.INBOUND);
  }
}
