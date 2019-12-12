package com.example.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Log4j2
@SpringBootApplication
public class HttpApplication {

  @Bean
  MapReactiveUserDetailsService userDetails() {
    UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
    return new MapReactiveUserDetailsService(user);
  }

  @Bean
  SecurityWebFilterChain authorization(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(x -> x.pathMatchers("/greeting*").authenticated())
        .httpBasic(Customizer.withDefaults())
        .build();
  }


  @Bean
  RouterFunction<ServerResponse> routes(GreetingService gs) {
    return route()
        .GET("/greeting", request -> {

          Mono<GreetingResponse> greetingResponseFlux = request
              .principal()
              .map(Principal::getName)
              .map(GreetingRequest::new)
              .flatMap(gs::greetOnce);
          return ServerResponse.ok().body(greetingResponseFlux, GreetingResponse.class);
        })
        .GET("/greetings", request -> {
          Flux<GreetingResponse> greetingResponseFlux = request
              .principal()
              .map(Principal::getName)
              .map(GreetingRequest::new)
              .flatMapMany(gs::greetMany);
          return ServerResponse
              .ok()
              .contentType(MediaType.TEXT_EVENT_STREAM)
              .body(greetingResponseFlux, GreetingResponse.class);
        })
        .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(HttpApplication.class, args);
  }
}


@Service
class GreetingService {

  Flux<GreetingResponse> greetMany(GreetingRequest request) {
    return Flux
        .fromStream(Stream.generate(() -> greet(request.getName())))
        .delayElements(Duration.ofSeconds(1))
        .subscribeOn(Schedulers.elastic());
  }

  Mono<GreetingResponse> greetOnce(GreetingRequest request) {
    return Mono.just(greet(request.getName()));
  }

  private GreetingResponse greet(String name) {
    return new GreetingResponse("Hello " + name + " @ " + Instant.now());
  }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {
  private String message;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
  private String name;
}

