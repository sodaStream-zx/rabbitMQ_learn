package rabbitcore.rabbit.TimeOutQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
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


    //    @RabbitListener(queues = "nomal_queue")
    @RabbitListener(queues = "timeOutRealIdQueue", concurrency = "1", containerFactory = "manualFactory")
    public void handleMeaage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, InterruptedException {
        channel.basicQos(1);
        String text = new String(message.getBody());
        JSONObject jsonObject = JSON.parseObject(text);
        System.out.println("收到的消息jsonObject :" + jsonObject.toString());
//        MessageProperties messageProperties = message.getMessageProperties();
        TimeUnit.SECONDS.sleep(1);
        channel.basicAck(tag, false);
    }
}


// messageProperties.setExpiration(String.valueOf(count * 100));
//         String ms = new String(message.getBody());
//         Message wrong = new Message((ms + " dddddlay").getBytes(), messageProperties);
//         rabbitTemplate.send("tll_exchange", "delay_key", wrong);
