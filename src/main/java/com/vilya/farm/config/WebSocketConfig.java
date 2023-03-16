package com.vilya.farm.config;

import com.vilya.farm.service.BearerTokenResolver;
import com.vilya.farm.service.TokenManager;
import com.vilya.farm.web.socket.handler.UnicastSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocket
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

  private final BearerTokenResolver tokenResolver;
  private final TokenManager tokenManager;
  private final UserDetailsService userDetailsService;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(new UnicastSocketHandler(), "/farm/{userId}")
        .setAllowedOrigins("*")
        .setHandshakeHandler(getHandshakeHandler())
        .addInterceptors(getInterceptor());
  }

  private HandshakeHandler getHandshakeHandler() {
    return new DefaultHandshakeHandler() {
      @Override
      protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = tokenResolver.resolve(request);
        if(StringUtils.isBlank(token)) {
          throw new BadCredentialsException("");
        }
        String username = tokenManager.parseClaims(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      }
    };
  }

  @Bean
  public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxTextMessageBufferSize(32768);
    container.setMaxBinaryMessageBufferSize(32768);
    return container;
  }

  private HandshakeInterceptor getInterceptor() {
    return new HandshakeInterceptor () {

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
