package com.example.reservationservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ReservationEntityTest {

  @Autowired
  private ReservationRepository reservationRepository;

  @Test
  public void persist() {
    Mono<Reservation> jane = this.reservationRepository
        .save(new Reservation(null, "Jane"));
    StepVerifier
        .create(jane)
        .expectNextMatches(reservation ->
            reservation.getName().equalsIgnoreCase("Jane") &&
                reservation.getId() != null)
        .verifyComplete();

  }
}
