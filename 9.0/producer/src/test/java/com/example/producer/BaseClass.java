package com.example.producer;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "server.port=0")
@RunWith(SpringRunner.class)
public class BaseClass {

  @MockBean
  private ReservationRepository reservationRepository;

  @LocalServerPort
  private int port;

  @Before
  public void before() {
    RestAssured.baseURI = "http://localhost:" + this.port;
    Mockito
        .when(this.reservationRepository.findAll())
        .thenReturn(Flux.just(new Reservation("1", "Jane")));
  }

}
