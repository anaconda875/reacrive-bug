package com.vilya.farm.service;

import com.vilya.farm.dto.ReceivedEvent;
import org.springframework.messaging.Message;

public interface UpstreamMessageConverter {
  Message<ReceivedEvent> toUpstreamMessage(Object data, String vid, String topic);

}
