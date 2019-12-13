package com.example.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Configuration
class ReservationHttpConfiguration {

  @Bean
  RouterFunction<ServerResponse> routes(ReservationRepository rr) {
    return route()
        .GET("/reservations", r -> ok().body(rr.findAll(), Reservation.class))
        .build();
  }
}
