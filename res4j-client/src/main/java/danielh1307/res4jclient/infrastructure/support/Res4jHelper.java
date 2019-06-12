package danielh1307.res4jclient.infrastructure.support;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.vavr.control.Try;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static io.vavr.control.Try.ofSupplier;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class Res4jHelper {

    private TimeLimiter timeLimiter;

    public Res4jHelper(TimeLimiter timeLimiter) {
        this.timeLimiter = timeLimiter;
    }

//    private <T> T executeWithRetriesAndTimeout(Supplier<T> supplier) throws Exception {
//        Supplier<T> decoratedSupplier = Retry.decorateSupplier(retry, supplier);
//
//        Try<T> executionResult = executeWithTimeout(
//                supplyAsync(() -> ofSupplier(decoratedSupplier))
//        );
//
//        return executionResult.get();
//    }

    public <T> T executeWithTimeout(Supplier<T> supplier) throws Exception {
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
