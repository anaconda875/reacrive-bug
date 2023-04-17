package com.vilya.farm.config;

import com.vilya.farm.dto.AmpMessage;
import com.vilya.farm.dto.ReceivedEvent;
import com.vilya.farm.service.AbstractAwaitService;
import com.vilya.farm.service.DownstreamMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class ConsumerBindingConfig {
  
  @Bean
  // PASS - kafka - topic.glcs.auth.response
  public Consumer<Message<String>> ampAuthResponseChannel(DownstreamMessageConverter downstreamMessageConverter,
                                                                 AbstractAwaitService service) {
    return msg -> {
      Mono.just(msg)
          .map(m -> downstreamMessageConverter.extractPayload(m, AmpMessage.class))
          .map(a -> a.getPayload().getAmpPayload())
          .doOnNext(a -> service.notifyResponse(a.getCid(), a))
          .subscribe(System.out::println, Throwable::printStackTrace);
    };
  }
}
