package danielh1307.res4jclient.ctrl;


import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Res4JManagementController {

    private CircuitBreakerRegistry circuitBreakerRegistry;

    Res4JManagementController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostMapping("/circuit-breaker/default/closed")
    public void changeToClosed() {
        this.circuitBreakerRegistry.circuitBreaker("backendA").transitionToClosedState();
    }

    @PostMapping("/circuit-breaker/default/reset")
    public void resetDefault() {
        this.circuitBreakerRegistry.circuitBreaker("backendA").reset();
    }
}
