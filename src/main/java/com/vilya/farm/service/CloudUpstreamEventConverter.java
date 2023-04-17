package com.vilya.farm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vilya.farm.dto.ReceivedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

import com.vilya.farm.dto.ReceivedEventData;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class CloudUpstreamEventConverter implements UpstreamMessageConverter {

  private final ObjectMapper objectMapper;

  public CloudUpstreamEventConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Message<ReceivedEvent> toUpstreamMessage(Object data, String vid, String topic) {
    ReceivedEventData dt =
        ReceivedEventData.builder()
            .deviceid(vid + "@" + vid)
            .uri(topic)
            .node("glcs")
            .retain(Boolean.FALSE)
            .dup(Boolean.FALSE)
            .payloadBase64(serializeData(data))
            .build();
    ReceivedEvent upstreamEvent = ReceivedEvent.builder().source("glcs").data(dt).build();

    return MessageBuilder.withPayload(upstreamEvent)
        .build();
  }
  
  public byte[] serializeData(Object data) {
    if (data instanceof byte[]) {
      return (byte[]) data;
    }
    if (data instanceof String) {
      return ((String) data).getBytes(StandardCharsets.UTF_8);
    }
    if (data instanceof Message) {
      return serializeData(((Message<?>) data).getPayload());
    }
    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
