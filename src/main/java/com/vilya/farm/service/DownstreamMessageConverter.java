package com.vilya.farm.service;

import org.springframework.messaging.Message;

public interface DownstreamMessageConverter {

  <T> Message<T> extractPayload(Message<String> source, Class<T> targetClass);
  
}
