package com.example.greetingsclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.rsocket.client.BrokerClient;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class GreetingsClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreetingsClientApplication.class, args);
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

@Log4j2
@Component
@RequiredArgsConstructor
class RSocketGatewayClient {

  private final BrokerClient client;

  @EventListener
  public void gatewayRsocketClient(PayloadApplicationEvent<RSocketRequester> event) {
    event
        .getPayload()
        .route("greetings.{time}", 2)
        .metadata(client.forwarding("greetings-service"))
        .data(new GreetingRequest("Livelessons"))
        .retrieveFlux(GreetingResponse.class)
        .subscribe(gr -> log.info(gr.toString()));
  }


}


