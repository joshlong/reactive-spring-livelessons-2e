package com.example.rsocketservice;

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
import java.util.stream.Stream;

@SpringBootApplication
public class RsocketServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(RsocketServiceApplication.class, args);
  }

}

@Controller
class GreetingService {

  @MessageMapping("greetings.{timeInSeconds}")
  Flux<GreetingResponse> greet(GreetingRequest request, @DestinationVariable int timeInSeconds) {
    if (timeInSeconds == 0 ) {
      timeInSeconds = 1 ;
    }
    return Flux
        .fromStream(Stream.generate(() -> new GreetingResponse("Hello " + request.getName() + " @ " + Instant.now() + "!")))
        .delayElements(Duration.ofSeconds(timeInSeconds));
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