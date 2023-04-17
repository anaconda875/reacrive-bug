package com.vilya.farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

import com.vilya.farm.dto.DispatchEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class CloudDownstreamEventConverter implements DownstreamMessageConverter {

  private final ObjectMapper objectMapper;

  public CloudDownstreamEventConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public <T> Message<T> extractPayload(Message<String> source, Class<T> targetClass) {
    String converted;
    try {
      converted =
          new String(
              objectMapper
                  .readValue(source.getPayload(), DispatchEvent.class)
                  .getData()
                  .getPayloadBase64(),
              StandardCharsets.UTF_8);
      MessageBuilder<T> messageBuilder =
          MessageBuilder.withPayload(objectMapper.readValue(converted, targetClass));

      return messageBuilder.setHeaders(new MessageHeaderAccessor(source)).build();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
