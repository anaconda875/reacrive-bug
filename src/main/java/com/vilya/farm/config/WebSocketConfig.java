package com.vilya.farm.config;

import com.vilya.farm.web.socket.handler.UnicastSocketHandler;
import com.vilya.farm.web.socket.service.CustomHandshakeWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig {

  //  private final BearerTokenResolver tokenResolver;
  //  private final TokenManager tokenManager;
  //  private final UserDetailsService userDetailsService;

  @Bean
  public HandlerMapping handlerMapping(UnicastSocketHandler unicastSocketHandler) {
    Map<String, WebSocketHandler> map = new HashMap<>();
    map.put("/farm/{userId}", unicastSocketHandler);

    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setUrlMap(map);
    mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return mapping;
  }

  @Bean
  public WebSocketHandlerAdapter handlerAdapter(WebSocketService service) {
    return new WebSocketHandlerAdapter(service);
  }

  @Bean
  public WebSocketService webSocketService() {
    return new CustomHandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
  }
}
