package danielh1307.res4jclient.ctrl;

import danielh1307.res4jclient.infrastructure.support.LogExecutionTime;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.vavr.control.Try;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static io.vavr.control.Try.ofSupplier;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
public class Res4JClientController {

    private RestTemplate restTemplate;
    private TimeLimiter timeLimiter;

    public Res4JClientController(RestTemplate restTemplate, TimeLimiter timeLimiter) {
        this.restTemplate = restTemplate;
        this.timeLimiter = timeLimiter;
    }

    @GetMapping("/res4jclient/host-not-available")
    @CircuitBreaker(name = "default")
    @Retry(name = "default")
    @LogExecutionTime
    public String hostNotAvailable() {
        Supplier<ResponseEntity<String>> supplier = () -> this.restTemplate.getForEntity("http://localhost:8888/res4jservice/mock", String.class);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = executeWithTimeout(supplier);
        } catch (HttpClientErrorException ex) {
            throw ex;
        } catch (TimeoutException ex) {
            throw new danielh1307.res4jclient.ctrl.TimeoutException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-delayed")
    @CircuitBreaker(name = "default")
    @Retry(name = "default")
    @LogExecutionTime
    public String hostDelayed() {
        Supplier<ResponseEntity<String>> supplier = () -> this.restTemplate.getForEntity("http://localhost:9080/res4jservice/delayed", String.class);


        ResponseEntity<String> responseEntity;
        try {
            responseEntity = executeWithTimeout(supplier);
        } catch (HttpClientErrorException ex) {
            throw ex;
        } catch (TimeoutException ex) {
            throw new danielh1307.res4jclient.ctrl.TimeoutException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-working")
    @CircuitBreaker(name = "default")
    @Retry(name = "default")
    @LogExecutionTime
    public String hostWorking() {
        Supplier<ResponseEntity<String>> supplier = () -> this.restTemplate.getForEntity("http://localhost:9080/res4jservice/working", String.class);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = executeWithTimeout(supplier);
        } catch (HttpClientErrorException ex) {
            throw ex;
        } catch (TimeoutException ex) {
            throw new danielh1307.res4jclient.ctrl.TimeoutException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-business-exception")
    @CircuitBreaker(name = "default")
    @Retry(name = "default")
    @LogExecutionTime
    public String hostBusinessException() {
        Supplier<ResponseEntity<String>> supplier = () -> this.restTemplate.getForEntity("http://localhost:9080/res4jservice/business-exception", String.class);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = executeWithTimeout(supplier);
        } catch (HttpClientErrorException ex) {
            throw ex;
        } catch (TimeoutException ex) {
            throw new danielh1307.res4jclient.ctrl.TimeoutException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-technical-exception")
    @CircuitBreaker(name = "default")
    @Retry(name = "default")
    @LogExecutionTime
    public String hostTechnicalException() {
        Supplier<ResponseEntity<String>> supplier = () -> this.restTemplate.getForEntity("http://localhost:9080/res4jservice/technical-exception", String.class);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = executeWithTimeout(supplier);
        } catch (HttpClientErrorException ex) {
            throw ex;
        } catch (TimeoutException ex) {
            throw new danielh1307.res4jclient.ctrl.TimeoutException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseEntity.getBody() + " Welt" + "\n";
    }

    private <T> T executeWithTimeout(Supplier<T> supplier) throws Exception {
        Try<T> executionResult;

        executionResult = executeWithTimeout(
                supplyAsync(() -> ofSupplier(supplier))
        );

        return executionResult.get();
    }

    private <T> T executeWithTimeout(CompletableFuture<T> completableFuture) throws Exception {
        return this.timeLimiter.executeFutureSupplier(() -> completableFuture);
    }
}
