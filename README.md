# Resilience4J

## Circuit Breaker

See https://resilience4j.readme.io/docs/circuitbreaker

The circuit breaker only records exceptions of type `recordExceptions`. If a call fails with a different exception, this is not recorded as a failed call.

See sample application.yml how to create different configurations and assign those configurations to specific CircuitBreaker instances.

There is also a `Res4JManagementController`which shows how the state of `CircuitBreaker`instances can be changed from outside.

	