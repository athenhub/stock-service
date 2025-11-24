package com.athenhub.stockservice.stock.domain.repository;

import com.athenhub.stockservice.stock.domain.StockHistory;
import com.athenhub.stockservice.stock.domain.vo.StockHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepository extends JpaRepository<StockHistory, StockHistoryId> {}
