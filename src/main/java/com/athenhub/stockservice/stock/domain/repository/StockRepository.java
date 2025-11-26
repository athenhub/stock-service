package com.athenhub.stockservice.stock.domain.repository;

import com.athenhub.stockservice.stock.domain.Stock;
import com.athenhub.stockservice.stock.domain.vo.ProductVariantId;
import com.athenhub.stockservice.stock.domain.vo.StockId;
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

  /**
   * 상품 옵션(Variant) ID를 기준으로 재고를 조회한다.
   *
   * <p>특정 상품 옵션에 대한 현재 재고 정보를 조회할 때 사용되며, 존재하지 않는 경우 {@link Optional#empty()}가 반환된다.
   *
   * @param variantId 상품 옵션(Variant) ID
   * @return 해당 옵션의 재고 정보
   * @author 김지원
   * @since 1.0.0
   */
  Optional<Stock> findByVariantId(ProductVariantId variantId);
}
