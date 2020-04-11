package danielh1307.res4jclient.ctrl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static io.vavr.control.Try.ofSupplier;
import static java.lang.String.format;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
public class Res4JClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Res4JClientController.class);

    private RestTemplate restTemplate;
    private TimeLimiter timeLimiter;

    public Res4JClientController(RestTemplate restTemplate, TimeLimiter timeLimiter) {
        this.restTemplate = restTemplate;
        this.timeLimiter = timeLimiter;
    }

    @GetMapping("/res4jclient/backendA/ok")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreaker() {
        return doServiceCall("http://localhost:9080/res4jservice/ok", String.class);
    }

    @GetMapping("/res4jclient/backendA/slow")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreakerSlow() {
        // this is NOT regarded as a "slow call" by resilience 4j (for backend A 4s is the limit)
        return doServiceCall("http://localhost:9080/res4jservice/ok-slow", String.class);
    }

    @GetMapping("/res4jclient/backendB/slow")
    @CircuitBreaker(name = "backendB")
    public String sampleCircuitBreakerSlowB() {
        // this is regarded as a "slow call" by resilience 4j (for backend B 1s is the limit)
        return doServiceCall("http://localhost:9080/res4jservice/ok-slow", String.class);
    }

    @GetMapping("/res4jclient/backendA/server-error")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreaker500() {
        return doServiceCall("http://localhost:9080/res4jservice/e500", String.class);
    }

    @GetMapping("/res4jclient/backendA/not-found")
    @CircuitBreaker(name = "backendA")
    public String sampleCircuitBreaker404() {
        // this call is ignored by resilience4j (HttpClientException)
        return doServiceCall("http://localhost:9080/res4jservice/e404", String.class);
    }

    @GetMapping("/res4jclient/backendA/delayed")
    @CircuitBreaker(name = "backendA")
    public String hostDelayed() {
        // this leads to a TimeoutException
        return doServiceCall("http://localhost:9080/res4jservice/delayed", String.class);
    }

    @GetMapping("res4jclient/backendA/retry404")
    @CircuitBreaker(name = "backendA")
    @Retry(name = "backendA")
    public String retry404BackendA() {
        // despite the method is annoted with @Retry, this should NOT lead to a retry here since we ignore HttpClientException
        return doServiceCall("http://localhost:9080/res4jservice/e404", String.class);
    }

    @GetMapping("res4jclient/backendA/retry500")
    @CircuitBreaker(name = "backendA")
    @Retry(name = "backendA")
    public String retry500BackendA() {
        // since we have annotated the method with @Retry, this should result in a retry here
        return doServiceCall("http://localhost:9080/res4jservice/e500", String.class);
    }

    @GetMapping("res4jclient/backendA/conn-refused")
    @CircuitBreaker(name = "backendA")
    @Retry(name = "backendA")
    public String connectionRefused() {
        return doServiceCall("http://localhost:8888/does-not-exist", String.class);
    }

    @SuppressWarnings("SameParameterValue")
    private <T> T doServiceCall(String url, Class<T> expectedResponseClass) {
        try {
            Supplier<ResponseEntity> supplier = () -> this.restTemplate.getForEntity(url, expectedResponseClass);
            ResponseEntity responseEntity = executeWithTimeout(supplier);

            return expectedResponseClass.cast(responseEntity.getBody());
        } catch (HttpClientErrorException ex) {
            LOGGER.error(format("For url %s, server responded with a client error exception: %s", url, ex.getMessage()));
            throw ex;
        } catch (HttpServerErrorException ex) {
            LOGGER.error(format("For url %s, server responded with a server exception: %s", url, ex.getMessage()));
            throw ex;
        } catch (java.util.concurrent.TimeoutException ex) {
            LOGGER.error(format("For url %s, server exceeded timeout", url));
            throw new TimeoutException();
        } catch (Exception ex) {
            LOGGER.error(format("For url %s, server responded with unknown exception: %s", url, ex.getMessage()));
            throw new GenericServiceException();
        }
    }

    private <T> T executeWithTimeout(Supplier<T> supplier) throws Exception {
        Try<T> executionResult; // represents either success or failure

        executionResult = executeWithTimeout(
                supplyAsync(() -> ofSupplier(supplier))
        );

        return executionResult.get(); // gets the result if it is a success, otherwise throws an error
    }

    private <T> T executeWithTimeout(CompletableFuture<T> completableFuture) throws Exception {
        // uses Future.get(long timeout, TimeUnit unit) internally
        // the method waits for at most the given time to execute
        // and since it is called on the calling thread, it blocks it
        return this.timeLimiter.executeFutureSupplier(() -> completableFuture);
    }
}
