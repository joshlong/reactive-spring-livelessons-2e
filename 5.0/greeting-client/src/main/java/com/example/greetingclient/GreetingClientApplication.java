package com.example.greetingclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
public class GreetingClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreetingClientApplication.class, args);
  }

  @Bean
  WebClient webClient(WebClient.Builder builder) {
    return builder
        .baseUrl("http://localhost:8080")
//				.filter( ExchangeFilterFunctions.basicAuthentication())
        .build();
  }

}

@Component
@Log4j2
//@RequiredArgsConstructor
class Client {

  private final WebClient client;
  private final ReactiveCircuitBreaker reactiveCircuitBreaker;

  Client(WebClient client, ReactiveCircuitBreakerFactory cbf) {
    this.client = client;
    this.reactiveCircuitBreaker = cbf.create("greeting");
  }

  @EventListener(ApplicationReadyEvent.class)
  public void ready() {


//    Flux<String> host1 = null;//todo
//    Flux<String> host2 = null;//todo
//    Flux<String> host3 = null;//todo
//
//    Flux<String> first = Flux.first(host1, host2, host3);

    var name = "Spring Fans";

    Mono<String> http = this.client
        .get()
        .uri("/greeting/{name}", name)
        .retrieve()
        .bodyToMono(GreetingResponse.class)
        .timeout(Duration.ofSeconds(10))
        .map(GreetingResponse::getMessage);

    this.reactiveCircuitBreaker
        .run(http, throwable -> Mono.just("EEEK!"))
        .subscribe(gr -> log.info("Mono: " + gr));

  }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
  private String name;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {
  private String message;
}