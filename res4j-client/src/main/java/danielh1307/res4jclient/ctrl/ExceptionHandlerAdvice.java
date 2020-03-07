package danielh1307.res4jclient.ctrl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.TimeoutException;

@ControllerAdvice
@ResponseBody
public class ExceptionHandlerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(value = TimeoutException.class)
    String handleTimeoutException(TimeoutException timeoutException, WebRequest webRequest) {
        return "Timed out";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundException.class)
    String handleNotFoundException(NotFoundException notFoundException, WebRequest webRequest) {
        return "Not found";
    }
}
