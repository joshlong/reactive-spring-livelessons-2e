<!-- Gateway itself -->
spring.rsocket.server.port=7002

<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-rsocket-dependencies</artifactId>
	<version>0.2.0.BUILD-SNAPSHOT</version>
	<type>pom</type>
	<scope>import</scope>
</dependency>

<!-- Gateway Broker -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-rsocket-broker</artifactId>
</dependency>

<!-- Gatway client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-rsocket-client</artifactId>
</dependency>

spring.rsocket.server.port=8888
server.port=8081
spring.application.name=greetings-service
spring.cloud.gateway.rsocket.client.service-name=greetings-service
spring.cloud.gateway.rsocket.client.route-id=3
spring.cloud.gateway.rsocket.client.broker.host=localhost
spring.cloud.gateway.rsocket.client.broker.port=7002

<!--  -->
@Bean
  ApplicationListener<PayloadApplicationEvent<RSocketRequester>> gatewayClient(BrokerClient client) {
    return event ->
        event
            .getPayload()
            .route("greetings")
            .metadata(client.forwarding("greetings-service"))
            .data(new GreetingRequest("World"))
            .retrieveFlux(GreetingResponse.class)
            .subscribe(gr -> log.info("gateway rsocket client: " + gr.getMessage()));
  }