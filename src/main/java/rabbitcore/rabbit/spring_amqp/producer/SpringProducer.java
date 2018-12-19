package rabbitcore.rabbit.spring_amqp.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rabbitcore.rabbit.spring_amqp.entity.User;

import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 * @desc 消息发送与接收
 * @createTime 2018-12-18-16:51
 */
@Component
public class SpringProducer {
    private static final Logger LOG = Logger.getLogger(SpringProducer.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    //发送消息
    public void sendMessage() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "send a message");
        messageProperties.getHeaders().put("type", 10);
        //tet
        messageProperties.setContentType("application/json");
        for (int i = 0; i < 10; i++) {
            //创建对象并序列化为json
            User user = new User(1, "zxx", "12345");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String userString = objectMapper.writeValueAsString(user);
                Message message = new Message(userString.getBytes(), messageProperties);
                rabbitTemplate.convertAndSend(message);
                //rabbitTemplate.send(message);
                LOG.info("发送消息 =======》》" + message.toString() + "\n");
                pause(1, 0);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    //container 消息监听
    public void reciveMessage() {
        LOG.info("\n发送消息完成，开始接收消息\n");
        // pause(5, 0);
        simpleMessageListenerContainer.start();
    }

    //休眠方法
    public void pause(int seconds, int mills) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}