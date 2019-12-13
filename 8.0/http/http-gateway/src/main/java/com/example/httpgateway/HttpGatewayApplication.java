package com.example.httpgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.PrincipalNameKeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.function.Predicate;

@SpringBootApplication
public class HttpGatewayApplication {

  @Bean
  RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(5, 7);
  }

  @Bean
  SecurityWebFilterChain authorization(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(Customizer.withDefaults())
        .authorizeExchange(x -> x.pathMatchers("/proxy").authenticated())
        .build();
  }

  @Bean
  MapReactiveUserDetailsService authentication() {
    return new MapReactiveUserDetailsService(
        User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
  }

  @Bean
  RouteLocator gateway(RouteLocatorBuilder rlb) {
    return rlb
        .routes()
        .route(routeSpec -> routeSpec
            .host("*.spring.io").and().path("/proxy")
            .filters(filterSpec -> filterSpec
                .setPath("/reservations")
                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .requestRateLimiter(rl -> rl
                    .setRateLimiter(redisRateLimiter())
                )
            )
            .uri("http://localhost:8080/")
        )
        .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(HttpGatewayApplication.class, args);
  }

}
