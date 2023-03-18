package com.vilya.farm.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtSetting {

  @Getter(AccessLevel.NONE)
  private String signingKey;

  private Key key;

  private Duration accessTokenExpiration;

  private String issuer;

  @PostConstruct
  public void init() {
    key =
        new SecretKeySpec(
            Decoders.BASE64.decode(signingKey), SignatureAlgorithm.HS512.getJcaName());
    ;
  }
}
