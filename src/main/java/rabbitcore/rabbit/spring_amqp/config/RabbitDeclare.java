package rabbitcore.rabbit.spring_amqp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 一杯咖啡
 * @desc rabbit 交换机，队列创建，绑定
 * @createTime 2018-12-16-14:37
 */

@Component
public class RabbitDeclare {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitDeclare.class);
    @Value("${ex.EXCHANGE_NAME}")
    private String EXCHANGE_NAME;
    @Value("${queue.QUEUE_NAME}")
    private String QUEUE_NAME;
    @Value("${queue.ROUNTING_KEY}")
    private String ROUNTING_KEY;

    @Autowired
    private AmqpAdmin amqpAdmin;

    public RabbitDeclare() {
    }

    public void myDeclareExchange() {
        //申明交换机
        amqpAdmin.declareExchange(new DirectExchange(EXCHANGE_NAME + "_direct", true, false, null));
        amqpAdmin.declareExchange(new FanoutExchange(EXCHANGE_NAME + "_fanout", true, false, null));
        amqpAdmin.declareExchange(new TopicExchange(EXCHANGE_NAME + "_topic", true, false, null));
        amqpAdmin.declareExchange(new HeadersExchange("header1", true, false, null));
        amqpAdmin.declareExchange(new HeadersExchange("header2", true, false, null));
        LOG.info("创建交换机完毕");
    }

    public void myDeclareQueue() {
        //申明队列
        for (int i = 0; i < 3; i++) {
            amqpAdmin.declareQueue(new Queue(QUEUE_NAME + i, true, false, false, null));
        }
        LOG.info("创建队列完毕");
    }


    public void myDeclareBinding() {
        //绑定交换机和队列
        amqpAdmin.declareBinding(new Binding(QUEUE_NAME + "0", Binding.DestinationType.QUEUE, EXCHANGE_NAME + "_direct", ROUNTING_KEY + "1", new HashMap<>()));
        amqpAdmin.declareBinding(new Binding(QUEUE_NAME + "1", Binding.DestinationType.QUEUE, EXCHANGE_NAME + "_fanout", ROUNTING_KEY + "2", new HashMap<>()));
        amqpAdmin.declareBinding(new Binding(QUEUE_NAME + "2", Binding.DestinationType.QUEUE, EXCHANGE_NAME + "_topic", ROUNTING_KEY + "3", new HashMap<>()));

        //创建并 header exchange
        Map<String, Object> headMap = new HashMap<>();
        headMap.put("type", 1);
        headMap.put("size", 10);
        amqpAdmin.declareBinding(BindingBuilder.bind(new Queue(QUEUE_NAME + "1")).to(new HeadersExchange("header1")).whereAll(headMap).match());
        amqpAdmin.declareBinding(BindingBuilder.bind(new Queue(QUEUE_NAME + "2")).to(new HeadersExchange("header2")).whereAny(headMap).match());
        LOG.info("创建绑定完毕完毕");
    }
}
