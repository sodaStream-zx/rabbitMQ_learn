package rabbitcore.rabbit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbitcore.rabbit.TimeOutQueue.ProProducer;

/**
 * @author Twilight
 * @desc
 * @createTime 2019-06-04-20:23
 */
@RestController
public class MsgController {
    @Autowired
    private ProProducer proProducer;

    @GetMapping(value = "/send")
    public String myTest() {
        proProducer.sendMessage();
        return "ok";
    }
}
