package com.example.rsocketclient;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class RsocketClientApplication {

  @Bean
  RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
    return builder.connectTcp("localhost", 7002).block();
  }

  @SneakyThrows
  public static void main(String[] args) {
    SpringApplication.run(RsocketClientApplication.class, args);
    System.in.read();
  }

}

@Component
@Log4j2
@RequiredArgsConstructor
class Client {

  private final RSocketRequester rSocketRequester;

  @EventListener(ApplicationReadyEvent.class)
  public void ready() {
    this.rSocketRequester
        .route("greetings.10")
        .data(new GreetingRequest("Livelessons"))
        .retrieveFlux(GreetingResponse.class)
        .subscribe(log::info);
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