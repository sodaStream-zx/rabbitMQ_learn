package rabbitcore.rabbit.TimeOutQueue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 一杯咖啡
 * @desc 交换机，队列 绑定
 * @createTime 2018-12-20-0:28
 */
@Configuration
public class BindConfig {
    //正常消费路由
    private static final String DELAY_EXCHANGE_NAME = "nomal_exchange";
    private static final String NOMAL_QUEUE = "nomal_queue";
    //正常队列key
    private static final String NOMAL_KEY = "nomal_key";

    //超时 异常转发路由
    private static final String TLL_EXCHANGE_NAME = "tll_exchange";
    // private static final String DELAY_QUEUE = "delay_queue";
    private static final String DELAY_KEY = "delay_key";

    //缓存队列
    private static final String DELAY_MESSAGE = "delay_message_queue";
    //死信转发路由
    private static final String DLX_EXCHANGE = "dlx_change";


    //队列超时时间
    // private static final long QUEUE_EXPIRATION = 6000;
    //正常消费队列


    @Bean
    public DirectExchange nomalExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME, true, false);
    }

    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(TLL_EXCHANGE_NAME, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    //消息超时队列
    @Bean
    public Queue delayMessageQueue() {
        return QueueBuilder.durable(DELAY_MESSAGE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", NOMAL_KEY)
                .withArgument("x-message-ttl", 5000)
                .build();
    }

    //消息超时队列
    @Bean
    public Queue nomalQueue() {
        return QueueBuilder.durable(NOMAL_QUEUE)
                .withArgument("x-dead-letter-exchange", TLL_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DELAY_KEY)
                .build();
    }
    //正常路由绑定 正常消费队列

    @Bean
    public Binding nomalBinding(Queue nomalQueue, DirectExchange nomalExchange) {
        return BindingBuilder.bind(nomalQueue).to(nomalExchange).with(NOMAL_KEY);
    }

    //配置超时队列路由绑定
    @Bean
    public Binding tllBinding(Queue delayMessageQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayMessageQueue).to(delayExchange).with(DELAY_KEY);
    }

    //死信队列绑定 正常路由
    @Bean
    public Binding nomalQueueBinding(Queue nomalQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(nomalQueue).to(dlxExchange).with(NOMAL_KEY);
    }
}

