package com.athenhub.stockservice.product.infrastructure.client;

import java.util.UUID;

public record ProductDetail(
        UUID hubId,
        UUID vendorId
) {

}
