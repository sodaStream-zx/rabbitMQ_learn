package rabbitcore.rabbit.rabbit_client.Consumer;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-16-3:46
 */
public class SimpleConsumer {
    private static final Logger LOG = Logger.getLogger(SimpleConsumer.class);

    private static final String EXCHANGE_NAME = "simple_exchange";
    private static final String ROUTING_KEY = "simple_key";
    private static final String QUEUE_NAME = "simple_queue";
    private static final String IP_ADDRESS = "localhost";
    private static final Integer PORT = 5672;
    private static final String VIRTUAL_Host = "springcloudhost";
    private static final String uri = "amqp://admin:admin" + "@" + IP_ADDRESS + ":" + PORT + "/" + VIRTUAL_Host;

    //设置消费者信息
    public static Map<String, Object> getInfo() {
        Map<String, Object> myInfo = new HashMap<>();
        myInfo.put("name", "消费者1");
        myInfo.put("author", "zxx");
        myInfo.put("email", "1139835238@qq.com");
        LOG.info("消费者信息 :: " + myInfo);
        return myInfo;
    }

    public static Connection getConnection() {
        Connection connection = null;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setClientProperties(getInfo());
        try {
            factory.setUri(uri);
            connection = factory.newConnection("simple 消费者");
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

    public static void publishMeaage(Connection con) throws IOException {
        Channel channel = con.createChannel(2);
        channel.basicQos(1);
        channel.basicConsume(QUEUE_NAME, false, "SIMPLE_CONSUMER", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                LOG.info("消息内容 :: "
                        + "\nconsumerTag = " + consumerTag
                        + "\nenvelop= " + envelope
                        + "\nproperties = " + properties
                        + "\nmessage = " + new String(body)
                        + "\n------------------");
                channel.basicAck(envelope.getDeliveryTag(), false);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        SimpleConsumer.publishMeaage(SimpleConsumer.getConnection());
    }
}
