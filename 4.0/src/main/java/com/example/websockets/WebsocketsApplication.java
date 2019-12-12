package com.example.websockets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootApplication
public class WebsocketsApplication {


  public static void main(String[] args) {
    SpringApplication.run(WebsocketsApplication.class, args);
  }

}

@Log4j2
@Configuration
class GreetingWebSocketConfiguration {

  @Bean
  SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler wsh) {
    return new SimpleUrlHandlerMapping(Map.of("/ws/greetings", wsh), 10);
  }

  @Bean
  WebSocketHandler webSocketHandler(GreetingService greetingService) {
    return session -> {
      var receive = session
          .receive()
          .map(WebSocketMessage::getPayloadAsText)
          .map(GreetingRequest::new)
          .flatMap(greetingService::greet)
          .map(GreetingResponse::getMessage)
          .map(session::textMessage)
          .doOnEach(signal -> log.info(signal.getType()))
          .doFinally(signal -> log.info("finally: " + signal.toString()));
      return session.send(receive);
    };
  }

  @Bean
  WebSocketHandlerAdapter webSocketHandlerAdapter() {
    return new WebSocketHandlerAdapter();
  }

}

@Service
class GreetingService {

  Flux<GreetingResponse> greet(GreetingRequest request) {
    return Flux
        .fromStream(Stream.generate(() -> new GreetingResponse("Hello " + request.getName() + " @ " + Instant.now())))
        .delayElements(Duration.ofSeconds(1));
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
