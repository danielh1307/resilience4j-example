# Resilience4J

See https://resilience4j.readme.io/docs/circuitbreaker

The project consists of two projects:
 * res4j-service: a service with different endpoints simulating different behaviour
 * res4j-client: a client which calls the service and whose calls are tracked by resilience4j

## res4j-service
The service is a Sprint Boot application and exposes serveral endpoints simulating specific behaviour:
 * One endpoint just returns a 200 OK immediately
 * One endpoint returns a 200 OK with delay (a few seconds, many seconds)
 * One endpoint returns a 404
 * One endpoint returns a 500
 * ... and so on ... just have a look at `Res4JServiceController`, it is very easy to understand

## res4j-client
The client is itself a Spring Boot application and calls the different endpoints. The calls are tracked by resilience4j. Currently the following features are used:
 * Circuit Breaker
 * Retry
 * Timeout

You can find the configuration for these in `application.yaml` (Circuit Breaker, Retry) and in `ApplicationConfiguration` (Timeout).

You can call the various endpoints from res4j-client from outside and see how resilience4j behaves:
 * /actuator/health: provides statistics about resilience4j
 * /actuator/metrics/resilience4j.circuitbreaker.calls: provides statistics about the calls of services which secured by circuit breakers
 * /actuator/metrics/resilience4j.retry.calls: provides statistics about the calls of services which needed a retry