package danielh1307.res4jclient;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class Resilience4JTest {

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void initialize() throws Exception {
        this.mockMvc.perform(post("/circuit-breaker/default/reset"));
    }

    @Test
    public void callsAreCorrectlyMeasured() throws Exception {
        callBackendAOk();
        assertThat(getCircuitBreakerMetric("backendA", "bufferedCalls", Integer.class), is(equalTo(1)));
        assertThat(getCircuitBreakerMetric("backendA", "failedCalls", Integer.class), is(equalTo(0)));

        callBackendAServerError();
        assertThat(getCircuitBreakerMetric("backendA", "bufferedCalls", Integer.class), is(equalTo(2)));
        assertThat(getCircuitBreakerMetric("backendA", "failedCalls", Integer.class), is(equalTo(1)));

        callBackendAClientError(); // this is ignored by resilience4j
        assertThat(getCircuitBreakerMetric("backendA", "bufferedCalls", Integer.class), is(equalTo(2)));
        assertThat(getCircuitBreakerMetric("backendA", "failedCalls", Integer.class), is(equalTo(1)));
    }

    @Test
    public void circuitClosesAfter5ErrorCalls() throws Exception {
        for (int i = 1; i <= 4; i++) {
            callBackendAServerError();
        }
        assertThat(getCircuitBreakerMetric("backendA", "state", String.class), is(equalTo("CLOSED")));

        callBackendAServerError();
        assertThat(getCircuitBreakerMetric("backendA", "state", String.class), is(equalTo("OPEN")));

        try {
            callBackendAOk();
            fail("Expected exception did not happen");
        } catch (NestedServletException ex) {
            if (!(ex.getCause() instanceof CallNotPermittedException)) {
                fail("Unexpected exception thrown: " + ex);
            }
        }
    }

    @Test
    public void circuitChangesAutomaticallyFromOpenToHalfOpen() throws Exception {
        closeCircuitBackendA();
        waitForHalfOpenBackendA();
        assertThat(getCircuitBreakerMetric("backendA", "state", String.class), is(equalTo("HALF_OPEN")));
    }

    @Test
    public void permit2CallsInHalfOpenState() throws Exception {
        closeCircuitBackendA();
        waitForHalfOpenBackendA();

        callBackendAOk();
        callBackendAOk();
        assertThat(getCircuitBreakerMetric("backendA", "state", String.class), is(equalTo("CLOSED")));
    }

    @Test
    public void slowCallsBackendABackendBDifferentBehavior() throws Exception {
        for (int i = 0; i < 5; i++) {
            callBackendASlow();
            callBackendBSlow();
        }

        assertThat(getCircuitBreakerMetric("backendA", "state", String.class), is(equalTo("CLOSED")));
        assertThat(getCircuitBreakerMetric("backendB", "state", String.class), is(equalTo("OPEN")));
    }

    private void callBackendAOk() throws Exception {
        this.mockMvc.perform(get("/res4jclient/backendA/ok"));
    }

    private void callBackendAClientError() throws Exception {
        try {
            this.mockMvc.perform(get("/res4jclient/backendA/not-found"));
            fail("Expected exception did not happen");
        } catch (NestedServletException ex) {
            if (!(ex.getCause() instanceof HttpClientErrorException)) {
                fail("Unexpected exception thrown: " + ex);
            }
        }
    }

    private void callBackendASlow() throws Exception {
        this.mockMvc.perform(get("/res4jclient/backendA/slow"));
    }

    private void callBackendBSlow() throws Exception {
        this.mockMvc.perform(get("/res4jclient/backendB/slow"));
    }

    private void callBackendAServerError() throws Exception {
        try {
            this.mockMvc.perform(get("/res4jclient/backendA/server-error"));
            fail("Expected exception did not happen");
        } catch (NestedServletException ex) {
            if (!(ex.getCause() instanceof HttpServerErrorException)) {
                fail("Unexpected exception thrown: " + ex);
            }
        }
    }

    private void closeCircuitBackendA() throws Exception {
        for (int i = 1; i <= 5; i++) {
            callBackendAServerError();
        }
    }

    private void waitForHalfOpenBackendA() throws Exception {
        assertThat(getCircuitBreakerMetric("backendA", "state", String.class), is(equalTo("OPEN")));
        Thread.sleep(25000);
        assertThat(getCircuitBreakerMetric("backendA", "state", String.class), is(equalTo("HALF_OPEN")));
    }

    private <T> T getCircuitBreakerMetric(String backend, String metric, Class<T> clazz) throws Exception {
        String contentAsString = this.mockMvc.perform(get("/actuator/health")).andReturn().getResponse().getContentAsString();

        JSONObject jsonObject = new JSONObject(contentAsString);

        Object o = jsonObject
                .getJSONObject("details")
                .getJSONObject("circuitBreakers")
                .getJSONObject("details")
                .getJSONObject(backend)
                .getJSONObject("details")
                .get(metric);

        return clazz.cast(o);
    }

}
