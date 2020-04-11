package danielh1307.res4jclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.NestedServletException;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class RetryTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    @SuppressWarnings("unchecked")
    public void successNoRetry() throws Exception {
        ResponseEntity<String> okEntity = new ResponseEntity<>("Hello", HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), any(Class.class))).thenReturn(okEntity);

        this.mockMvc.perform(get("/res4jclient/backendA/ok"))
                .andExpect(status().isOk());

        verify(this.restTemplate, times(1)).getForEntity(anyString(), any(Class.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedWithServerErrorFourRetries() throws Exception {
        when(restTemplate.getForEntity(anyString(), any(Class.class))).thenThrow(new HttpServerErrorException(INTERNAL_SERVER_ERROR));

        try {
            this.mockMvc.perform(get("/res4jclient/backendA/retry500"));
        } catch (NestedServletException ex) {
            if (!(ex.getCause() instanceof HttpServerErrorException)) {
                fail("Unexpected excpetion thrown");
            }
        }

        verify(this.restTemplate, times(4)).getForEntity(anyString(), any(Class.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedWithClientErrorNoRetries() throws Exception {
        when(restTemplate.getForEntity(anyString(), any(Class.class))).thenThrow(new HttpClientErrorException(BAD_REQUEST));

        try {
            this.mockMvc.perform(get("/res4jclient/backendA/retry404"));
        } catch (NestedServletException ex) {
            if (!(ex.getCause() instanceof HttpClientErrorException)) {
                fail("Unexpected excpetion thrown");
            }
        }

        verify(this.restTemplate, times(1)).getForEntity(anyString(), any(Class.class));
    }

}
