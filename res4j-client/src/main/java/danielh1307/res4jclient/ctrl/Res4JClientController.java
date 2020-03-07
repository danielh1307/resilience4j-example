package danielh1307.res4jclient.ctrl;

import danielh1307.res4jclient.infrastructure.support.LogExecutionTime;
import danielh1307.res4jclient.infrastructure.support.Res4jHelper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class Res4JClientController {

    private RestTemplate restTemplate;
    private Res4jHelper res4jHelper;

    Res4JClientController(RestTemplate restTemplate, Res4jHelper res4jHelper) {
        this.restTemplate = restTemplate;
        this.res4jHelper = res4jHelper;
    }

    @GetMapping("/res4jclient/sample-cb")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreaker() throws Exception {
        // as long as record exceptions are only GenericServiceExceptions, all other errors are not tracked by resilience4j
        // so for example if the service is not available (500 connection refused) this does not activate the circuit breaker
        return this.restTemplate.getForEntity("http://localhost:8081/res4jservice/ok", String.class).getBody() + "\n";
    }

    @GetMapping("/res4jclient/sample-cb-slow")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreakerSlow() throws Exception {
        // as long as record exceptions are only GenericServiceExceptions, all other errors are not tracked by resilience4j
        // so for example if the service is not available (500 connection refused) this does not activate the circuit breaker
        return this.restTemplate.getForEntity("http://localhost:8081/res4jservice/ok-slow", String.class).getBody() + "\n";
    }

    @GetMapping("/res4jclient/sample-cb-slow-r")
    @CircuitBreaker(name = "backendB")
    public String sampleCircuitBreakerSlowRestricted() throws Exception {
        // as long as record exceptions are only GenericServiceExceptions, all other errors are not tracked by resilience4j
        // so for example if the service is not available (500 connection refused) this does not activate the circuit breaker
        return this.restTemplate.getForEntity("http://localhost:8081/res4jservice/ok-slow", String.class).getBody() + "\n";
    }

    @GetMapping("/res4jclient/sample-cb500")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreaker500() throws Exception {
        // here we are throwing a GenericServiceException in any case, except for a 404
        // so all errors are tracked by resilience4j
        try {
            return this.restTemplate.getForEntity("http://localhost:8081/res4jservice/e500", String.class).getBody() + "\n";
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == NOT_FOUND) {
                throw new NotFoundException();
            }
            throw new GenericServiceException();
        } catch (Exception ex) {
            throw new GenericServiceException();
        }
    }

    @GetMapping("/res4jclient/sample-cb404")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreaker404() throws Exception {
        try {
            return this.restTemplate.getForEntity("http://localhost:8081/res4jservice/e404", String.class).getBody() + "\n";
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == NOT_FOUND) {
                throw new NotFoundException();
            }
            throw new GenericServiceException();
        } catch (Exception ex) {
            throw new GenericServiceException();
        }
    }

    @GetMapping("/res4jclient/sample-retry")
    @CircuitBreaker(name = "backendA")
    @LogExecutionTime
    public String sampleServiceRetry() throws Exception {
        Supplier<ResponseEntity<String>> supplier = () -> {
            System.out.println("Calling service ...");
            return this.restTemplate.getForEntity("http://localhost:8081/res4jservice/mock", String.class);
        };

        ResponseEntity<String> responseEntity = this.res4jHelper.executeWithRetriesAndTimeout(supplier);

        return responseEntity.getBody() + " Welt" + "\n";
    }

}
