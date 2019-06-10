package danielh1307.res4jservice.ctrl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Res4JServiceController {

    @GetMapping("/res4jservice/mock")
    public String mockService() throws InterruptedException {

//        Thread.sleep(15000);

//        throw new BusinessException();

        return "Hello";
    }

}
