package com.vilya.farm.authenticcation;

import com.vilya.farm.service.BearerTokenResolver;
import com.vilya.farm.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ServerBearerAuthenticationConverter implements ServerAuthenticationConverter {

  protected final BearerTokenResolver tokenResolver;
  protected final TokenManager tokenManager;
  protected final ReactiveUserDetailsService userDetailsService;

  @Override
  public Mono<Authentication> convert(ServerWebExchange exchange) {
    return Mono.fromCallable(
            () -> {
              String token = tokenResolver.resolve(exchange);
              if (token == null) {
                return Mono.<Authentication>empty();
              }
              String username = tokenManager.parseClaims(token).getBody().getSubject();
              return userDetailsService
                  .findByUsername(username)
                  .map(u -> new UsernamePasswordAuthenticationToken(u, token, u.getAuthorities()))
                  .zipWhen(auth -> exchange.getSession())
                  .map(
                      t -> {
                        t.getT1()
                            .setDetails(
                                new WebAuthenticationDetails(
                                    exchange.getRequest().getRemoteAddress().toString(),
                                    t.getT2().getId()));
                        return t.getT1();
                      });
            })
        .onErrorReturn(Mono.empty())
        .flatMap(Function.identity());
  }
}
