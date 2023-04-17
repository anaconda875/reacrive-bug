package com.vilya.farm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vilya.farm.dto.DispatchEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
//@Primary
public class HotPublisherBasedAwaitService extends AbstractAwaitService {
  
  public static final ConcurrentMap<String, Sinks.One<Object>> SINKS = new ConcurrentHashMap<>();
  
  public HotPublisherBasedAwaitService(StreamBridge streamBridge, UpstreamMessageConverter upstreamMessageConverter,
                                       @Value("${spring.cloud.stream.bindings.ampAuthResponseChannel-in-0.destination}")
                                       String ampAuthResponseChannel) {
    super(streamBridge, upstreamMessageConverter, ampAuthResponseChannel);
  }
  
  @Override
  protected Mono<Object> awaitResponse(String cid) {
    Sinks.One<Object> sink = Sinks.one();
    SINKS.put(cid, sink);
    
    return sink.asMono().subscribeOn(Schedulers.boundedElastic());
  }
  
  @Override
  public void notifyResponse(String cid, Object obj) {
    SINKS.computeIfPresent(cid, (k, v) -> {
      System.out.println("RECEIVED MESSAGE: " + cid);
      v.tryEmitValue(obj);
      return null;
    });
  }
}
