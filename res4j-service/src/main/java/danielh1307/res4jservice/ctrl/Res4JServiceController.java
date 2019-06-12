package danielh1307.res4jservice.ctrl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Res4JServiceController {

    @GetMapping("/res4jservice/working")
    public String working() {
        return "Hello";
    }

    @GetMapping("/res4jservice/delayed")
    public String delayed() throws InterruptedException {
        Thread.sleep(15000);
        return "Hello";
    }

    @GetMapping("/res4jservice/business-exception")
    public String businessException() {
        throw new BusinessException();
    }

    @GetMapping("/res4jservice/technical-exception")
    public String technicalException() {
        throw new RuntimeException();
    }

}
