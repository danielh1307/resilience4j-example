package danielh1307.res4jclient.ctrl;

import danielh1307.res4jclient.infrastructure.support.LogExecutionTime;
import danielh1307.res4jclient.infrastructure.support.Res4jHelper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@RestController
public class Res4JClientController {

    private RestTemplate restTemplate;
    private Res4jHelper res4jHelper;

    Res4JClientController(RestTemplate restTemplate, Res4jHelper res4jHelper) {
        this.restTemplate = restTemplate;
        this.res4jHelper = res4jHelper;
    }

    @GetMapping("/res4jclient/host-not-available")
    @CircuitBreaker(name = "default")
    @LogExecutionTime
    public String hostNotAvailable() {
        Supplier<ResponseEntity<String>> supplier = () -> {
            System.out.println("Calling service ...");
            return this.restTemplate.getForEntity("http://localhost:8888/res4jservice/mock", String.class);
        };

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = this.res4jHelper.executeWithRetriesAndTimeout(supplier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-delayed")
    @CircuitBreaker(name = "default")
    @LogExecutionTime
    public String hostDelayed() {
        Supplier<ResponseEntity<String>> supplier = () -> {
            System.out.println("Calling service ...");
            return this.restTemplate.getForEntity("http://localhost:9080/res4jservice/delayed", String.class);
        };

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = this.res4jHelper.executeWithRetriesAndTimeout(supplier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-working")
    @CircuitBreaker(name = "default")
    @LogExecutionTime
    public String hostWorking() {
        Supplier<ResponseEntity<String>> supplier = () -> {
            System.out.println("Calling service ...");
            return this.restTemplate.getForEntity("http://localhost:9080/res4jservice/working", String.class);
        };

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = this.res4jHelper.executeWithRetriesAndTimeout(supplier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-business-exception")
    @CircuitBreaker(name = "default")
    @LogExecutionTime
    public String hostBusinessException() {
        Supplier<ResponseEntity<String>> supplier = () -> {
            System.out.println("Calling service ...");
            return this.restTemplate.getForEntity("http://localhost:9080/res4jservice/business-exception", String.class);
        };

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = this.res4jHelper.executeWithRetriesAndTimeout(supplier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responseEntity.getBody() + " Welt" + "\n";
    }

    @GetMapping("/res4jclient/host-technical-exception")
    @CircuitBreaker(name = "default")
    @LogExecutionTime
    public String hostTechnicalException() {
        Supplier<ResponseEntity<String>> supplier = () -> {
            System.out.println("Calling service ...");
            return this.restTemplate.getForEntity("http://localhost:9080/res4jservice/technical-exception", String.class);
        };

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = this.res4jHelper.executeWithRetriesAndTimeout(supplier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responseEntity.getBody() + " Welt" + "\n";
    }

}
