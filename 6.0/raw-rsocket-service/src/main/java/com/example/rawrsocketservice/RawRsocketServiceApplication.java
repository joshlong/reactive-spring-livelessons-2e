package com.example.rawrsocketservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class RawRsocketServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(RawRsocketServiceApplication.class, args);
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

@Component
@RequiredArgsConstructor
class JsonHelper {

  private final ObjectMapper objectMapper;

  @SneakyThrows
  <T> T read(String json, Class<T> clzz) {
    return this.objectMapper.readValue(json, clzz);
  }

  @SneakyThrows
  String write(Object o) {
    return this.objectMapper.writeValueAsString(o);
  }
}

@Log4j2
@Component
@RequiredArgsConstructor
class Producer {

  private final JsonHelper jsonHelper;

  private Flux<GreetingResponse> greet(GreetingRequest request) {
    return Flux
        .fromStream(Stream.generate(
            () -> new GreetingResponse("Hello " + request.getName() + " @ " + Instant.now().toString())))
        .delayElements(Duration.ofSeconds(1));
  }

  @EventListener(ApplicationReadyEvent.class)
  public void start() {
    log.info("producer starting...");
    SocketAcceptor socketAcceptor = (connectionSetupPayload, sender) -> {

      AbstractRSocket abstractRSocket = new AbstractRSocket() {

        @Override
        public Flux<Payload> requestStream(Payload payload) {
          String json = payload.getDataUtf8();
          GreetingRequest greetingRequest = jsonHelper.read(json, GreetingRequest.class);
          return greet(greetingRequest)
              .map(jsonHelper::write)
              .map(DefaultPayload::create);
        }
      };

      return Mono.just(abstractRSocket);
    };

    TcpServerTransport transport = TcpServerTransport.create(7000);

    RSocketFactory
        .receive()
        .acceptor(socketAcceptor)
        .transport(transport)
        .start()
        .block();
  }
}
