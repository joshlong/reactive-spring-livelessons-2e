package com.example.reservationclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
class ReservationClient {

  private final WebClient webClient;

  Flux<Reservation> getAllReservations() {
    return this.webClient
        .get()
        .uri("http://localhost:8080/reservations")
        .retrieve()
        .bodyToFlux(Reservation.class);
  }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {
  private String id, name;
}