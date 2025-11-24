package com.athenhub.stockservice.stock.infrastructure.dto;

import com.athenhub.stockservice.stock.infrastructure.MemberRole;
import com.athenhub.stockservice.stock.infrastructure.MemberStatus;
import com.athenhub.stockservice.stock.infrastructure.OrganizationType;
import java.time.LocalDateTime;
import java.util.UUID;

public record MemberInfo(
    UUID id,
    String name,
    String username,
    String slackId,
    UUID organizationId,
    OrganizationType organizationType,
    String organizationName,
    MemberRole role,
    MemberStatus status,
    LocalDateTime deletedAt,
    String deletedBy) {}
