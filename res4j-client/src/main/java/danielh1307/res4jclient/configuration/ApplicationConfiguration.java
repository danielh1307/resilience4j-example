package danielh1307.res4jclient.configuration;

import danielh1307.res4jclient.infrastructure.support.Res4jHelper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(10000))
                .build();
    }

    @Bean
    public TimeLimiter timeLimiter(TimeLimiterConfig timeLimiterConfig) {
        return TimeLimiter.of(timeLimiterConfig);
    }

    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(4)
                .waitDuration(Duration.ofMillis(2000))
                .retryExceptions(HttpServerErrorException.class, IOException.class, ConnectException.class, ResourceAccessException.class)
                .build();
    }

    @Bean
    public Retry retry(RetryConfig retryConfig) {
        return Retry.of("id", retryConfig);
    }

    @Bean
    public Res4jHelper res4jHelper(TimeLimiter timeLimiter, Retry retry) {
        return new Res4jHelper(timeLimiter, retry);
    }

}
