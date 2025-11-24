package com.athenhub.stockservice.stock.infrastructure.client;

import com.athenhub.stockservice.stock.infrastructure.config.FeignClientConfig;
import com.athenhub.stockservice.stock.infrastructure.dto.MemberInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/members")
@FeignClient(name = "member-service", configuration = FeignClientConfig.class)
public interface MemberClient {
  @GetMapping("/me")
  MemberInfo getMyInfo();
}
