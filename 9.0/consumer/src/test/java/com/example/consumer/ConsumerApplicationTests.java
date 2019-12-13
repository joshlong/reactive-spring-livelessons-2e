package com.example.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(
    ids = "com.example:producer:+:8080",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class ConsumerApplicationTests {

  @Autowired
  private ReservationClient client;

  @Test
  public void contextLoads() {
    Flux<Reservation> reservations = this.client.getAllReservations();
    StepVerifier
        .create(reservations)
        .expectNextMatches(reservation ->
            reservation.getId() != null && reservation.getName().equalsIgnoreCase("Jane"))
        .verifyComplete();
  }

}
