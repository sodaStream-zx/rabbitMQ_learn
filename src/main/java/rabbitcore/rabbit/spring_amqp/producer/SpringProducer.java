package rabbitcore.rabbit.spring_amqp.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import rabbitcore.rabbit.spring_amqp.config.RabbitDeclare;
import rabbitcore.rabbit.spring_amqp.entity.User;
import rabbitcore.rabbit.utils.PauseUtil;

/**
 * @author 一杯咖啡
 * @desc 消息发送与接收
 * @createTime 2018-12-18-16:51
 */
//@Component
public class SpringProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SpringProducer.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitDeclare rabbitDeclare;
    @Autowired
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    //发送消息
    public void sendMessage() {
        rabbitDeclare.myDeclareExchange();
        rabbitDeclare.myDeclareQueue();
        rabbitDeclare.myDeclareBinding();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "send a message");
        messageProperties.getHeaders().put("type", 10);
        //tet
        messageProperties.setContentType("application/json");
//        for (int i = 0; i < 10; i++) {
        while (true) {
            //创建对象并序列化为json
            User user = new User(1, "zxx", "12345");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String userString = objectMapper.writeValueAsString(user);
                Message message = new Message(userString.getBytes(), messageProperties);
                rabbitTemplate.convertAndSend(message);
                //rabbitTemplate.send(message);
                LOG.info("发送消息 =======》》" + message.toString() + "\n");
                PauseUtil.pause(1, 0);
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
}