package com.athenhub.stockservice.product.fixture;

import com.athenhub.stockservice.product.domain.Stock;
import com.athenhub.stockservice.product.domain.exception.StockDomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Stock permission related tests.
 *
 * @author 김지원
 * @since 1.0.0
 */
class StockPermissionTest {

    @Test
    void cannot_register_stock_if_user_does_not_belong_to_hub_or_vendor() {
        assertThatThrownBy(StockFixture::createNotBelongingUser)
                .isInstanceOf(StockDomainException.class);
    }

    @Test
    void cannot_register_stock_if_user_has_no_product_permission() {
        assertThatThrownBy(StockFixture::createNoPermission)
                .isInstanceOf(StockDomainException.class);
    }

    @Test
    void stock_is_created_when_user_has_belongs_and_permission() {
        Stock stock = StockFixture.create();

        assertThat(stock.getQuantity()).isEqualTo(10);
    }
}
