package com.vilya.farm.web.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vilya.farm.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class UnicastSocketHandler implements WebSocketHandler {

  private final ObjectMapper objectMapper;
  //  List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return session
        .receive()
        .map(WebSocketMessage::getPayloadAsText)
        .map(LambdaUtil.asSneakyThrowFunction(objectMapper::readTree))
        .doOnNext(System.out::println)
        .then();
  }
}
