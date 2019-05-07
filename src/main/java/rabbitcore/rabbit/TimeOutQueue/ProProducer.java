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
        //消息到交换机失败回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.out.println("发送失败");
            }
        });
        //消息到队列失败回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println(message.getBody() + "发送失败");
        });
        int count = 1;
        while (count < 20) {
            //正常消息设置超时时间
            messageProperties.setExpiration(String.valueOf(count * 1000));
            messageProperties.setTimestamp(new Date());
//            发送到正常消费队列
            Message message = new Message(("realID" + count).getBytes(), messageProperties);
            rabbitTemplate.send("WaiteExchange", "waite", message, new CorrelationData(UUID.randomUUID().toString()));
            System.out.println("发送消息到rabbit");
//            if (count % 2 == 0) {
//                Message message = new Message("delayMessage".getBytes(), messageProperties);
//                rabbitTemplate.send("nomal_exchange", "nomal_key", message, new CorrelationData(UUID.randomUUID().toString()));
//            } else {
//                Message message = new Message("nomalMessage".getBytes(), messageProperties);
//                rabbitTemplate.send("nomal_exchange", "nomal_key", message, new CorrelationData(UUID.randomUUID().toString()));
//            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }
}
