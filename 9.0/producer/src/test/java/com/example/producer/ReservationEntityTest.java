package com.example.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class ReservationEntityTest {

  @Autowired
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @Test
  public void persist() {
    Reservation reservation = new Reservation(null, "Jane");
    Mono<Reservation> savedRecord = this.reactiveMongoTemplate.save(reservation);
    StepVerifier
        .create(savedRecord)
        .expectNextMatches(res -> res.getId() != null && res.getName().equalsIgnoreCase("Jane"))
        .verifyComplete();

  }
}
