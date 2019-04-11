package rabbitcore.rabbit.rabbit_client.Consumer;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 一杯咖啡
 * @desc 消息消费者
 * @createTime 2018-12-15-0:44
 */
public class Myconsumer {
    private static final String EXCHANGE_NAME = "simple_exchange";
    private static final String QUEUE_NAME = "simple_queue";
    private static final String IP_ADDRESS = "10.253.90.20";
    private static Integer PORT = 5672;
    private static final Logger LOG = Logger.getLogger(Myconsumer.class);

    public static void main(String[] args) {
        //建立连接信息
        Address[] address = new Address[]{new Address(IP_ADDRESS, PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("yunce");

        try {

            Connection connection = factory.newConnection(address);
            Channel channel = connection.createChannel();
            //判断信道时候就绪,未就绪会抛出异常
            channel.exchangeDeclarePassive(EXCHANGE_NAME);
            //设置该消费者获取消息，最大unack 数量。。超过该数值，将无法继续获取消息
            channel.basicQos(5);
            //自动确认消息已被消费 autoACK = false
            // channel.basicConsume(QUEUE_NAME, false, "Myconsumer", true, false, null, new ConsumerObj(channel));
//            for (int i = 0; i < 20; i++) {
            while (true) {
                GetResponse message = channel.basicGet(QUEUE_NAME, false);
                if (message == null) {
                    break;
                }
                LOG.info("1 ." + message.toString()
                        + "\n2 .message == " + new String(message.getBody())
                        + "\n ------------------  ");
                //mutiple = true 确定此条消息前所有消息。
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                TimeUnit.SECONDS.sleep(1);
            }
            //运行basicconsume 后直接关闭连接。会直接关闭消息获取。
            LOG.info("消息未获取到 或者已到最大获取数");
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.info("交换机不存在");
        } catch (TimeoutException e) {
            e.printStackTrace();
            LOG.info("shudown2");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
