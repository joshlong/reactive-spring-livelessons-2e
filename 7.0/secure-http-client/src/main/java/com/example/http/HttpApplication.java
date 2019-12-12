package com.example.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class HttpApplication {

  @Bean
  WebClient webClient(WebClient.Builder builder) {
    return builder
        .filter(ExchangeFilterFunctions.basicAuthentication("user", "password"))
        .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(HttpApplication.class, args);
  }
}

@Component
@Log4j2
@RequiredArgsConstructor
class Consumer {

  private final WebClient client;

  @EventListener(ApplicationReadyEvent.class)
  public void start() {
    this.client
        .get()
        .uri("http://localhost:8080/greetings")
        .retrieve()
        .bodyToFlux(GreetingResponse.class)
        .subscribe(log::info);
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

