package com.example.reservationclient;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(
    ids = "com.example:reservation-service:+:8080",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class ReservationClientApplicationTests {

  @Autowired
  private ReservationClient client;

  @Test
  public void contextLoads() {

    /*
    stubFor(get(
        urlEqualTo("/reservations"))
        .willReturn(aResponse()
            .withBody("[ {\"id\" : \"1\" , \"name\":\"Jane\"}  ]")
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        ));
    */
    Flux<Reservation> allReservations = this.client.getAllReservations();

    StepVerifier
        .create(allReservations)
        .expectNextMatches(reservation -> reservation.getId() != null && reservation.getName().equalsIgnoreCase("Jane"))
        .verifyComplete();

  }

}
