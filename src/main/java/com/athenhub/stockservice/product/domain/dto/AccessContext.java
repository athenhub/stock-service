package com.athenhub.stockservice.product.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AccessContext(@NotNull UUID memberId, UUID hubId, UUID vendorId) {}
