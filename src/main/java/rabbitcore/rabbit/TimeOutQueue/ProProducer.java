package rabbitcore.rabbit.TimeOutQueue;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-20-0:39
 */
@Component
public class ProProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage() {
        MessageProperties messageProperties = new MessageProperties();
        // messageProperties.setContentType("text");
        int count = 1;
        while (true) {
            //正常消息设置死信队列
            messageProperties.setExpiration(String.valueOf(count * 100));
            messageProperties.setTimestamp(new Date());
            if (count % 2 == 0) {
                Message message = new Message("delayMessage".getBytes(), messageProperties);
                rabbitTemplate.send("nomal_exchange", "nomal_key", message, new CorrelationData(UUID.randomUUID().toString()));
            } else {
                Message message = new Message("nomalMessage".getBytes(), messageProperties);
                rabbitTemplate.send("nomal_exchange", "nomal_key", message, new CorrelationData(UUID.randomUUID().toString()));
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }
}
