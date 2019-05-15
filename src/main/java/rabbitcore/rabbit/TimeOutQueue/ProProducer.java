package rabbitcore.rabbit.TimeOutQueue;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

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
        rabbitTemplate.setMandatory(true);
        int count = 1;
        while (count < 10) {
            //正常消息设置超时时间
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("realId", count);
            Message message = MessageBuilder.withBody(jsonObject.toJSONString().getBytes())
                    .setExpiration(String.valueOf(1000 * 60L))
                    .setTimestamp(new Date())
                    .setContentType("application/json")
                    .build();
            rabbitTemplate.send("WaiteExchange", "waite", message, new CorrelationData(UUID.randomUUID().toString()));
            System.out.println("发送消息到rabbit");
            count++;
        }
    }
}

//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
