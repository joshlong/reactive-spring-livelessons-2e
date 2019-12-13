package com.example.greetingsservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootApplication
public class GreetingsServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreetingsServiceApplication.class, args);
  }
}

@Controller
class GreetingsRSocketController {

  @MessageMapping("greetings.{timeInSeconds}")
  Flux<GreetingResponse> greet(@DestinationVariable int timeInSeconds, GreetingRequest greetingRequest) {
    return Flux
        .fromStream(Stream.generate(() -> new GreetingResponse("Hello " + greetingRequest.getName() + " @ " + Instant.now() + "!")))
        .delayElements(Duration.ofSeconds(timeInSeconds));
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