package com.vilya.farm.config;

import com.vilya.farm.web.socket.handler.UnicastSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Map;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(new UnicastSocketHandler(), "farm/{userId}")
        .setAllowedOrigins("*")
        .addInterceptors(getInterceptor());
  }

  @Bean
  public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxTextMessageBufferSize(32768);
    container.setMaxBinaryMessageBufferSize(32768);
    return container;
  }

  private HandshakeInterceptor getInterceptor() {
    return new HandshakeInterceptor() {

      @Override
      public boolean beforeHandshake(
          ServerHttpRequest serverHttpRequest,
          ServerHttpResponse serverHttpResponse,
          WebSocketHandler webSocketHandler,
          Map<String, Object> map) {
        String path = serverHttpRequest.getURI().getPath();
        log.info("Start handshake in path: {}", path);
        String[] arr = path.split("/");
        try {
          String id = arr[2];
          log.info("User: {} handshake...", id);
          map.put("userId", id);
          return true;
        } catch (Exception e) {
          log.error(e.toString());
          return false;
        }
      }

      @Override
      public void afterHandshake(
          ServerHttpRequest request,
          ServerHttpResponse response,
          WebSocketHandler wsHandler,
          Exception exception) {
        log.info("{} handshake complete", request.getURI().getPath());
      }
    };
  }
}
