package com.athenhub.stockservice.product.infrastructure.dto;

import com.athenhub.stockservice.product.infrastructure.MemberRole;
import com.athenhub.stockservice.product.infrastructure.MemberStatus;
import com.athenhub.stockservice.product.infrastructure.OrganizationType;
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
