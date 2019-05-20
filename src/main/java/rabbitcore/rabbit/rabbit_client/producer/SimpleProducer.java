package rabbitcore.rabbit.rabbit_client.producer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-16-3:28
 */
public class SimpleProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleProducer.class);
    private static final String EXCHANGE_NAME = "simple_exchange";
    private static final String ROUTING_KEY = "simple_key";
    private static final String QUEUE_NAME = "simple_queue";
    private static final String IP_ADDRESS = "ycrabbitmq.dc.zz";
    private static final Integer PORT = 5672;
    private static final String VIRTUAL_Host = "yunce";
    private static final String uri = "amqp://admin:admin" + "@" + IP_ADDRESS + ":" + PORT + "/" + VIRTUAL_Host;

    //获取连接
    public static Connection getConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = null;
        try {
            factory.setUri(uri);
            connection = factory.newConnection("simple 生产者");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //建立信道，绑定交换机，队列
    public static Channel initExchangeAndQueue(Connection connection) {
        Channel channel = null;
        try {
            channel = connection.createChannel(1);
            AMQP.Exchange.DeclareOk exchangeOk = channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);
            AMQP.Queue.DeclareOk queueOk = channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            LOG.info("交换机创建成功 == " + exchangeOk);
            LOG.info("队列创建成功 == " + queueOk);
            channel.exchangeDeclarePassive(EXCHANGE_NAME);
            channel.queueDeclarePassive(QUEUE_NAME);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            AMQP.Exchange.DeclareOk exchangeOk2 = channel.exchangeDeclare(EXCHANGE_NAME + "_1", "direct", true, false, null);
            AMQP.Queue.DeclareOk queueOk2 = channel.queueDeclare(QUEUE_NAME + "_1", true, false, false, null);
            LOG.info("交换机创建成功 == " + exchangeOk2);
            LOG.info("队列创建成功 == " + queueOk2);
            channel.exchangeDeclarePassive(EXCHANGE_NAME + "_1");
            channel.queueDeclarePassive(QUEUE_NAME + "_1");
            channel.queueBind(QUEUE_NAME + "_1", EXCHANGE_NAME + "_1", ROUTING_KEY + "_1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public static void confirmModel(Channel channel) {
    }

    //发送消息
    public static void publishMessage() throws IOException, InterruptedException {
        Connection connection = getConnection();
        Channel channel = initExchangeAndQueue(connection);
        try {
            channel.confirmSelect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AMQP.BasicProperties.Builder myBasicProperties = new AMQP.BasicProperties().builder();
        AMQP.BasicProperties properties = myBasicProperties.contentType("text/plain").deliveryMode(2).priority(1).userId("admin").build();
        LOG.info("消息properties == " + myBasicProperties.toString());
//        try {
        while (true) {
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, properties, "hello Simple string".getBytes());
            if (!channel.waitForConfirms()) {
                LOG.error("send message failed");
            } else {
                LOG.info("send message successed");
            }
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, false, properties, "are you ok??".getBytes());
            if (!channel.waitForConfirms()) {
                LOG.error("send message failed");
            } else {
                LOG.info("send message successed");
            }
            channel.basicPublish(EXCHANGE_NAME + "_1", ROUTING_KEY + "_1", false, properties, "thank you ,i am fine!!!!".getBytes());
            if (!channel.waitForConfirms()) {
                LOG.error("send message faile");
            } else {
                LOG.info("send message successed");
            }
            TimeUnit.MILLISECONDS.sleep(15);
        }

    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(5));
        //线程池满，直接在调用线程中执行该任务
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                try {
                    publishMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
