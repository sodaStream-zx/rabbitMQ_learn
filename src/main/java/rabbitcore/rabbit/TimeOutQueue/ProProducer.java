package rabbitcore.rabbit.TimeOutQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-20-0:39
 */
@Component
public class ProProducer {
    private static final Logger log = LoggerFactory.getLogger(ProProducer.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage() {
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
                System.out.println(message.toString() + " " + replyCode + replyText + exchange + routingKey));
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) ->
                System.out.println(correlationData.toString() + ack + cause));
        int count = 1;
        //正常消息设置超时时间
        News news = new News();
        news.setId(1L);
        news.setAuthor("zxx" + count);
        news.setTitle("这是标题" + count);
        news.setContent("dasdasdasdasdasdwvsd" + count);
        news.setSource("四川" + count);
        news.setTime(LocalDateTime.now());
        String jsonString = JSON.toJSONString(news, SerializerFeature.PrettyFormat);
        Message message = MessageBuilder.withBody(jsonString.getBytes())
                .setAppId("this is appID")
                .setCorrelationId(UUID.randomUUID().toString())
                .setContentEncoding("utf8")
                .setType("this type")
                .setUserId("admin")
                .setExpiration(String.valueOf(1000 * 60L))
                .setTimestamp(new Date())
                .setContentType("application/json")
                .build();

        rabbitTemplate.send(BindDelayConfig.waiteExchange, BindDelayConfig.waiteKey, message, new CorrelationData(UUID.randomUUID().toString()));
        log.warn("发送消息到rabbit");
    }
}


