package rabbitcore.rabbit.spring_amqp.spring_consumer;

import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rabbitcore.rabbit.spring_amqp.entity.User;

import java.io.IOException;
import java.util.Map;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-19-14:42
 */
@Component
//@RabbitListener(queues = "spring_rb_queue1")
public class AnnatotionConusmer {
    private static final Logger LOG = Logger.getLogger(AnnatotionConusmer.class);

    /*@RabbitHandler
    public void revicer2(Message message) {
        LOG.info("收到消息 =======》》\nmessage ==" + message.toString() + "\n");

    }*/
    //@RabbitListener(queues = "spring_rb_queue1")
    public void recevier1(@Payload User user, @Headers Map<String, Object> map, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        LOG.info("收到消息 =======》》\nmessage == " + user.toString() + "\nmap == " + map + "\n");
        try {
            channel.basicAck(tag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
