package com.vilya.farm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@Primary
public class ColdPublisherBasedAwaitService extends AbstractAwaitService {
  
  public static final ConcurrentMap<String, Listener> LISTENERS = new ConcurrentHashMap<>();
  
  public ColdPublisherBasedAwaitService(StreamBridge streamBridge, UpstreamMessageConverter upstreamMessageConverter,
                                        @Value("${spring.cloud.stream.bindings.ampAuthResponseChannel-in-0.destination}")
                                       String ampAuthResponseChannel) {
    super(streamBridge, upstreamMessageConverter, ampAuthResponseChannel);
  }
  
  @Override
  protected Mono<Object> awaitResponse(String cid) {
    return Mono.fromCallable(() -> {
      CountDownLatch countDownLatch = new CountDownLatch(1);
      LISTENERS.put(cid, new Listener(countDownLatch));
      boolean result = countDownLatch.await(10, TimeUnit.SECONDS);
      if(result) {
        Mono<Object> mono = Mono.just(LISTENERS.get(cid).message);
        LISTENERS.remove(cid);
        
        return mono;
      }
      return Mono.<Object>error(new RuntimeException("Time out"));
    }).flatMap(Function.identity()).subscribeOn(Schedulers.boundedElastic());
  }
  
  @Override
  public void notifyResponse(String cid, Object obj) {
    System.out.println("RECEIVED MESSAGE: " + cid);
    LISTENERS.get(cid).message = obj;
    LISTENERS.get(cid).countDownLatch.countDown();
  }
  
  private static class Listener {
    CountDownLatch countDownLatch;
    Object message;
  
    public Listener(CountDownLatch countDownLatch) {
      this.countDownLatch = countDownLatch;
    }
  }
}
