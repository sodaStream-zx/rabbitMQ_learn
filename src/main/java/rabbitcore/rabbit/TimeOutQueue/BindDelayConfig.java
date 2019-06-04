package rabbitcore.rabbit.TimeOutQueue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zxx
 * @desc
 * @createTime 2019-05-08-上午 11:44
 */
@Configuration
public class BindDelayConfig {
    //等待队列
    public static final String waiteQueue = "waiteQueue";
    //监听队列
    public static final String TimeOutQueue = "timeOutQueue";

    //等待交换机
    public static final String waiteExchange = "waiteExchange";
    public static final String waiteKey = "waite";
    //死信交换机
    public static final String TimeOutExchange = "timeOutExchange";
    public static final String timeOutKey = "timeout";

    @Bean
    public DirectExchange waiteExchange() {
        return new DirectExchange(waiteExchange);
    }

    @Bean
    public DirectExchange timeOutExchange() {
        return new DirectExchange(TimeOutExchange);
    }

    //消息超时队列
    @Bean
    public Queue waiteQueue() {
        return QueueBuilder.durable(waiteQueue)
                .withArgument("x-dead-letter-exchange", TimeOutExchange)
                .withArgument("x-dead-letter-routing-key", timeOutKey)
                .build();
    }

    //消息超时队列
    @Bean
    public Queue TimeOutQueue() {
        return QueueBuilder.durable(TimeOutQueue)
                .build();
    }

    //配置超时队列路由绑定
    @Bean
    public Binding waiteBinding(Queue waiteQueue, DirectExchange waiteExchange) {
        return BindingBuilder.bind(waiteQueue).to(waiteExchange).with(waiteKey);
    }

    //配置超时队列路由绑定
    @Bean
    public Binding timeOutBinding(Queue TimeOutQueue, DirectExchange timeOutExchange) {
        return BindingBuilder.bind(TimeOutQueue).to(timeOutExchange).with(timeOutKey);
    }
}
