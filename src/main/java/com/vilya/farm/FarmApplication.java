package com.vilya.farm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableReactiveMongoRepositories
public class FarmApplication {

  public static void main(String[] args) {
    SpringApplication app =
        new SpringApplicationBuilder(FarmApplication.class)
            .web(WebApplicationType.REACTIVE)
            .build();
    app.run(args);
  }
}
