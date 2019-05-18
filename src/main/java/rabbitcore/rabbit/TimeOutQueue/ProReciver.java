package rabbitcore.rabbit.TimeOutQueue;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
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


    //    @RabbitListener(queues = "nomal_queue")
    @RabbitListener(queues = "timeOutRealIdQueue", concurrency = "1", containerFactory = "manualFactory")
    public void handleMeaage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        channel.basicQos(1);
        String text = new String(message.getBody());
//        LOG.warn("收到的消息jsonObject :" + text);
        News news = JSON.parseObject(text, News.class);
        LOG.warn("JsonToObject 转化后" + news.toString());
//        PauseUtil.pause(1);
        channel.basicAck(tag, false);
    }
}
