package danielh1307.res4jclient.infrastructure.support;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionTimeAspect {

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        System.out.println("Calling " + proceedingJoinPoint.getSignature() + " ...");

        Object proceed;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            System.out.println("Call to " + proceedingJoinPoint.getSignature() + " not successful: " + t);
            throw t;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            System.out.println(proceedingJoinPoint.getSignature() + " executed in " + executionTime + "ms");
        }

        return proceed;
    }

}
