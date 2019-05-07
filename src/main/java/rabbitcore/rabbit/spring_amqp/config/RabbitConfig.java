package rabbitcore.rabbit.spring_amqp.config;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import rabbitcore.rabbit.spring_amqp.config.messageUtils.MyMessageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 一杯咖啡
 * @desc rabbitmq 工具配置
 * @createTime 2018-12-16-15:22
 */
//@Configuration
public class RabbitConfig {
    private Logger LOG = Logger.getLogger(RabbitConfig.class);
    @Value("${ex.EXCHANGE_NAME}")
    private String EXCHANGE_NAME;
    @Value("${queue.QUEUE_NAME}")
    private String QUEUE_NAME;
    @Value("${queue.ROUNTING_KEY}")
    private String ROUNTING_KEY;
    private String IP_ADDRESS = "ycrabbitmq.dc.zz";
    private Integer PORT = 5672;
    private String VIRTUAL_Host = "yunce";
    private Integer consumer_num = 0;

    //注释后：使用默认连接 properties 中
    /*@Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        String uri = "amqp://admin:admin" + "@" + IP_ADDRESS + ":" + PORT + "/" + VIRTUAL_Host;
        cachingConnectionFactory.setUri(uri);
        cachingConnectionFactory.setPublisherReturns(true);
        return cachingConnectionFactory;
    }*/

   /* @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
*/
   /* @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }*/

    //自定义rabbitTemplate 取代默认连接 properties
    /*@Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //设置消息转换
        rabbitTemplate.setMessageConverter(messageConverter);
        //设置默认交换机和key
        rabbitTemplate.setExchange(EXCHANGE_NAME + "_fanout");
        //rabbitTemplate.setRoutingKey(ROUNTING_KEY + 2);
        //rabbitTemplate.setChannelTransacted(true);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, b, s) -> LOG.info(
                "确认消息：------>return confirm call back "
                        +"\ncorrelationdata = "+correlationData.toString()
                        + "\n ack = " + b
                        + "\ncaused = " + s));
        rabbitTemplate.setReturnCallback((message, i, s, s1, s2) -> LOG.info(
                "路由到消费者队列失败----》" +
                        "\nmessage=" + message
                        + "\nreplayCode=" + i
                        + "\nreplatText=" + s
                        + "\nexchange=" + s1
                        + "\nrountingkey=" + s2));
        return rabbitTemplate;
    }*/

   /* @Bean
    public RabbitTemplate.ConfirmCallback getConfirmCallback() {
        return (correlationData, b, s) -> LOG.info(correlationData.toString() + "\nb = " + b + "\ns = " + s);
    }*/

    //rabbit -client 消息监听
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(QUEUE_NAME + 1);
        //设置consumerTAG
        container.setConsumerTagStrategy(s -> "consumer" + (consumer_num++));

        Map<String, Object> consumerArge = new HashMap<>();
        consumerArge.put("function", "获取消息");
        //设置消费者信息
        container.setConsumerArguments(consumerArge);
        //设置并发数
        container.setConcurrentConsumers(5);
        //设置最大未确认数
        container.setPrefetchCount(10);
        //设置手动确认消息
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MyMessageHandler());
        //设置默认处理方法
        // adapter.setDefaultListenerMethod("handleMessage");
        //为不同队列设置不同处理方法
        Map<String, String> map = new HashMap<>();
        map.put(QUEUE_NAME + 0, "onMessage");
        map.put(QUEUE_NAME + 1, "onMessage");
        map.put(QUEUE_NAME + 2, "onMessage");
        adapter.setQueueOrTagToMethodName(map);
        container.setMessageListener(adapter);
       /*
       container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
           LOG.info("----接收到 message = "+new String(message.getBody())+"\nchannel = "+channel);
           LOG.info("delivery = "+message.getMessageProperties().getDeliveryTag());
           TimeUnit.SECONDS.sleep(1);
           channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
       });*/
        LOG.info("---------->>>CONTAINER == \n" + container.toString() + "\n");
        container.setAutoStartup(false);
        return container;
    }

    //spring rabbit 消息监听
    /*@Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setPrefetchCount(5);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConsumerTagStrategy(s -> s + 1);
        return factory;
    }*/
}
