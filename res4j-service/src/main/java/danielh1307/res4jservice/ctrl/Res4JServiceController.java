package danielh1307.res4jservice.ctrl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Res4JServiceController {

    /**
     *
     * @return immediately HTTP 200
     */
    @GetMapping("/res4jservice/ok")
    public String mockService() {
        return "Hello";
    }

    @GetMapping("/res4jservice/ok-slow")
    public String mockServiceSlow() throws InterruptedException{
        Thread.sleep(3000);
        return "Hello";
    }

    /**
     *
     * @return immediately HTTP 500
     */
    @GetMapping("/res4jservice/e500")
    public String error500() {
        throw new NullPointerException();
    }

    /**
     *
     * @return immediately HTTP 404
     */
    @GetMapping("/res4jservice/e404")
    public String error404() {
        throw new BusinessException();
    }


    @GetMapping("/res4jservice/delayed")
    public String delayed() throws InterruptedException {
        Thread.sleep(15000);
        return "Hello";
    }

}
