package com.example.greetingservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RouterFunctions.resourceLookupFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@SpringBootApplication
public class GreetingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreetingServiceApplication.class, args);
  }

  @Bean
  RouterFunction<ServerResponse> routes(GreetingService greetingService) {
    return route()
        .GET("/greeting/{name}", serverRequest -> ok().body(
            greetingService.greetOnce(new GreetingRequest(serverRequest.pathVariable("name"))), GreetingResponse.class))
        .GET("/greetings/{name}", serverRequest -> ok().contentType(MediaType.TEXT_EVENT_STREAM).body(
            greetingService.greetMany(new GreetingRequest(serverRequest.pathVariable("name"))), GreetingResponse.class))
        .build();
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

@Service
class GreetingService {

  Mono<GreetingResponse> greetOnce(GreetingRequest request) {
    return Mono.just(greet(request.getName()));
  }

  Flux<GreetingResponse> greetMany(GreetingRequest request) {
    return Flux
        .fromStream(Stream.generate(() -> greet(request.getName())))
        .delayElements(Duration.ofSeconds(1));
  }

  private GreetingResponse greet(String name) {
    return new GreetingResponse("Hello " + name + "  @ " + Instant.now() + "!");
  }
}
