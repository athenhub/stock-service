package com.athenhub.stockservice.product.infrastructure;

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
