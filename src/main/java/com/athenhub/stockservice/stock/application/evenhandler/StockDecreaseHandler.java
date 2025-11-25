package com.athenhub.stockservice.stock.application.evenhandler;

import com.athenhub.commoncore.error.GlobalErrorCode;
import com.athenhub.stockservice.global.infrastructure.springevent.Events;
import com.athenhub.stockservice.stock.application.exception.StockApplicationException;
import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.dto.StockDecreaseRequest;
import com.athenhub.stockservice.stock.domain.event.internal.StockDecreasedEvent;
import com.athenhub.stockservice.stock.domain.repository.StockRepository;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class StockDecreaseHandler {

  private final StockRepository stockRepository;

  public void decrease(StockDecreaseRequest request) {
    Stock stock = stockRepository.findByProductIdAndVariantId(ProductId.of(request.productId()),
            ProductVariantId.of(request.variantId()))
        .orElseThrow(() -> new StockApplicationException(GlobalErrorCode.NOT_FOUND));
    
    // 재고 차감
    stock.decrease(request.quantity());

    // 이벤트 발행
    Events.trigger(StockDecreasedEvent.from(stock));
  }
}
