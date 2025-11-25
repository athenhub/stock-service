package com.athenhub.stockservice.stock.domain.repository;

import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.vo.ProductId;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import com.athenhub.stockservice.stock.domain.vo.StockId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 재고(Stock) 도메인의 영속성 처리를 담당하는 Repository이다.
 *
 * <p>재고 등록, 조회, 수정(증가/감소/삭제) 시 사용되며, 기본적인 CRUD 기능은 {@link JpaRepository}를 통해 제공된다.
 *
 * <p>필요 시 비즈니스 목적에 맞는 커스텀 조회 메서드를 추가하여 확장할 수 있다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, StockId> {

  List<Stock> findByProductId(ProductId productId);

  Optional<Stock> findByVariantId(ProductVariantId variantId);
}
