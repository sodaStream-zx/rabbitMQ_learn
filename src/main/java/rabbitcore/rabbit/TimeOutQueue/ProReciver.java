package rabbitcore.rabbit.TimeOutQueue;

import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-20-0:37
 */
@Component
public class ProReciver {
    private static final Logger LOG = Logger.getLogger(ProReciver.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //    @RabbitListener(queues = "nomal_queue")
    @RabbitListener(queues = {"timeOutRealIdQueue"})
    public void handleMeaage(Message message, Channel channel) throws IOException {

        int count = 1;
        String text = new String(message.getBody());
        System.out.println("收到的消息：： " + text + "发送到nomalQueue");
        MessageProperties messageProperties = message.getMessageProperties();
        messageProperties.setExpiration(String.valueOf(count * 100));
        String ms = new String(message.getBody());
        Message wrong = new Message((ms + " dddddlay").getBytes(), messageProperties);
        rabbitTemplate.send("tll_exchange", "delay_key", wrong);
        channel.basicAck(messageProperties.getDeliveryTag(), false);
    }
//        TimeUnit.SECONDS.sleep(1);
}
