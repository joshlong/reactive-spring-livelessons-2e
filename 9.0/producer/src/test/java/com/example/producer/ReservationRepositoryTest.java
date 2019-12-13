package com.example.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class ReservationRepositoryTest {

  @Autowired
  private ReservationRepository repository;

  @Test
  public void persist() throws Exception {

    Flux<Reservation> reservationFlux = this.repository.saveAll(
        Flux.just(new Reservation(null, "A"),
        new Reservation(null, "B"),
        new Reservation(null, "C"),
        new Reservation(null, "A")));

    StepVerifier
        .create(this.repository.deleteAll())
        .verifyComplete();

    StepVerifier
        .create(reservationFlux)
        .expectNextCount(4)
        .verifyComplete();

    StepVerifier
        .create(this.repository.findByName("A"))
        .expectNextCount(2)
        .verifyComplete();

  }

}
