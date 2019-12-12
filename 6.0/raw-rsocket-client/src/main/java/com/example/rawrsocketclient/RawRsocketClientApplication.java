package com.example.rawrsocketclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
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

@SpringBootApplication
public class RawRsocketClientApplication {

  @SneakyThrows
  public static void main(String[] args) {
    SpringApplication.run(RawRsocketClientApplication.class, args);
    System.in.read();
  }

}

@Log4j2
@RequiredArgsConstructor
@Component
class Consumer {

  private final JsonHelper jsonHelper;

  @EventListener(ApplicationReadyEvent.class)
  public void start() {

    log.info("consumer starting...");

    var request = jsonHelper.write(new GreetingRequest("Livelessons"));

    RSocketFactory
        .connect()
        .transport(TcpClientTransport.create(7000))
        .start()
        .flatMapMany(sender -> sender
            .requestStream(DefaultPayload.create(request))
            .map(Payload::getDataUtf8)
            .map(json -> jsonHelper.read(json, GreetingResponse.class))
        )
        .subscribe(result -> log.info("processing new result " + result.toString()));
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
