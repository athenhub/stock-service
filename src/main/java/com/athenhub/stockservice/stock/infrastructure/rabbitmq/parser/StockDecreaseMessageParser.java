package com.athenhub.stockservice.stock.infrastructure.rabbitmq.parser;

import com.athenhub.stockservice.stock.application.dto.StockDecreaseBatchEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class StockDecreaseMessageParser {

  private final ObjectMapper objectMapper;

  public StockDecreaseMessageParser(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public StockDecreaseBatchEvent parse(Message message) throws Exception {
    return objectMapper.readValue(message.getBody(), StockDecreaseBatchEvent.class);
  }
}
