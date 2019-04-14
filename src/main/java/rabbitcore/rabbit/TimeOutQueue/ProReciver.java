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
import java.util.concurrent.TimeUnit;


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

    @RabbitListener(queues = "nomal_queue")
    public void handleMeaage(Message message, Channel channel) throws InterruptedException, IOException {
        int count = 1;
        String text = new String(message.getBody());
        if ("delayMessage".equalsIgnoreCase(text)) {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setExpiration(String.valueOf(count * 100));
            String ms = new String(message.getBody());
            Message wrong = new Message((ms + " dddddlay").getBytes(), messageProperties);
            rabbitTemplate.send("tll_exchange", "delay_key", wrong);
            channel.basicAck(messageProperties.getDeliveryTag(), false);
        } else {
            LOG.info("message = " + text);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        TimeUnit.SECONDS.sleep(1);
    }
}
