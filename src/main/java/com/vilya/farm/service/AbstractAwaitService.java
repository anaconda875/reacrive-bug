package com.vilya.farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vilya.farm.dto.AmpMessage;
import com.vilya.farm.dto.AuthAmpPayload;
import com.vilya.farm.dto.DispatchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class AbstractAwaitService {
  
  protected final StreamBridge streamBridge;
  protected final UpstreamMessageConverter upstreamMessageConverter;
  protected final String ampAuthResponseChannel;
  
  public Mono<Object> process(String vin, String cid) {
    return Mono.fromCallable(() -> {
//    preProcess(cid, authenticationData);
      sendRequest(vin, cid);
      System.out.println(Thread.currentThread().getName());
  
      return awaitResponse(cid);
    }).flatMap(Function.identity());
  }
  
  protected abstract Mono<Object> awaitResponse(String cid);
  
  public abstract void notifyResponse(String cid, Object obj);
  
  private void sendRequest(String vin, String cid) {
    AuthAmpPayload authAmpPayload = new AuthAmpPayload();
    authAmpPayload.setVin(vin);
    authAmpPayload.setCid(cid);
    authAmpPayload.setResponseTopic(ampAuthResponseChannel);
    streamBridge.send("topic.glcs.ampOutput", upstreamMessageConverter
        .toUpstreamMessage(AmpMessage.builder().p(Collections.singletonList(authAmpPayload)).build(),
        vin, "/%s/%s".formatted(vin, "auth")));
  }
  
}
