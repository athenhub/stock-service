package com.athenhub.stockservice.stock.infrastructure.client.member;

public enum OrganizationType {
  NONE,
  HUB,
  VENDOR;

  public boolean isVendor() {
    return this == VENDOR;
  }

  public boolean isHub() {
    return this == HUB;
  }
}
