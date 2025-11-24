package com.athenhub.stockservice.global.infrastructure.rabbitmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rabbit.stock")
public class RabbitStockProperties {
  private String exchange;
  private String queue;
  private String routingKey;
}
