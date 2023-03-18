package com.vilya.farm.web.socket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CustomHandshakeWebSocketService extends HandshakeWebSocketService {

  private static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
  private static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

  private final RequestUpgradeStrategy upgradeStrategy;

  @Override
  public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
    ServerHttpRequest request = exchange.getRequest();
    HttpMethod method = request.getMethod();
    HttpHeaders headers = request.getHeaders();

    if (HttpMethod.GET != method) {
      return Mono.error(
          new MethodNotAllowedException(
              request.getMethod(), Collections.singleton(HttpMethod.GET)));
    }

    if (!"WebSocket".equalsIgnoreCase(headers.getUpgrade())) {
      return handleBadRequest(exchange, "Invalid 'Upgrade' header: " + headers);
    }

    List<String> connectionValue = headers.getConnection();
    if (!connectionValue.contains("Upgrade") && !connectionValue.contains("upgrade")) {
      return handleBadRequest(exchange, "Invalid 'Connection' header: " + headers);
    }

    String key = headers.getFirst(SEC_WEBSOCKET_KEY);
    if (key == null) {
      return handleBadRequest(exchange, "Missing \"Sec-WebSocket-Key\" header");
    }

    String protocol = selectProtocol(headers, handler);

    return initAttributes(exchange)
        .flatMap(
            attributes ->
                this.upgradeStrategy.upgrade(
                    exchange,
                    handler,
                    protocol,
                    () -> createHandshakeInfo(exchange, request, protocol, attributes)));
  }

  protected HandshakeInfo createHandshakeInfo(
      ServerWebExchange exchange,
      ServerHttpRequest request,
      @Nullable String protocol,
      Map<String, Object> attributes) {

    URI uri = request.getURI();
    // Copy request headers, as they might be pooled and recycled by
    // the server implementation once the handshake HTTP exchange is done.
    HttpHeaders headers = new HttpHeaders();
    headers.addAll(request.getHeaders());
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
    Mono<Principal> principal = exchange.getPrincipal();
    String logPrefix = exchange.getLogPrefix();
    InetSocketAddress remoteAddress = request.getRemoteAddress();
    return new HandshakeInfo(
        uri, headers, cookies, principal, protocol, remoteAddress, attributes, logPrefix);
  }

  protected Mono<Map<String, Object>> initAttributes(ServerWebExchange exchange) {
    return exchange
        .getSession()
        .map(
            session -> {
              Map<String, Object> attributes = session.getAttributes();
              String path = exchange.getRequest().getURI().getPath();
              log.info("Start handshake in path: {}", path);
              UriTemplate uriTemplate = new UriTemplate("/farm/{userId}"); // create template
              Map<String, String> parameters =
                  uriTemplate.match(path); // extract values form template
              attributes.putAll(parameters);

              return attributes;
            });
  }

  @Nullable
  protected String selectProtocol(HttpHeaders headers, WebSocketHandler handler) {
    String protocolHeader = headers.getFirst(SEC_WEBSOCKET_PROTOCOL);
    if (protocolHeader != null) {
      List<String> supportedProtocols = handler.getSubProtocols();
      for (String protocol : StringUtils.commaDelimitedListToStringArray(protocolHeader)) {
        if (supportedProtocols.contains(protocol)) {
          return protocol;
        }
      }
    }
    return null;
  }

  protected Mono<Void> handleBadRequest(ServerWebExchange exchange, String reason) {
    if (log.isDebugEnabled()) {
      log.debug(exchange.getLogPrefix() + reason);
    }
    return Mono.error(new ServerWebInputException(reason));
  }
}
