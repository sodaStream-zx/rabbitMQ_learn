package rabbitcore.rabbit.spring_amqp.spring_consumer;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-19-14:42
 */
@Component
@RabbitListener(queues = "spring_rb_queue1")
public class AnnatotionConusmer {
    private static final Logger LOG = Logger.getLogger(AnnatotionConusmer.class);

    @RabbitHandler
    public void revicer2(Map message) {
        LOG.info("收到消息 =======》》\nmessage ==" + message.toString() + "\n");

    }
    /*@RabbitListener(queues = "spring_rb_queue1")
    public void recevier1(@Payload User user, @Headers Map<String,Object> map){
        LOG.info("收到消息 =======》》\nmessage ==" + user.toString() +"\nmap == " +map+"\n");
    }*/
}
