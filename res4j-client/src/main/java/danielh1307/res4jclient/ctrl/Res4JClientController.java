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

    @GetMapping("/res4jclient/sample-retry")
    @CircuitBreaker(name = "default")
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
