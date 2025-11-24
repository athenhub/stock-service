package com.athenhub.stockservice.stock.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AccessContext(@NotNull UUID memberId, UUID hubId, UUID vendorId) {}
