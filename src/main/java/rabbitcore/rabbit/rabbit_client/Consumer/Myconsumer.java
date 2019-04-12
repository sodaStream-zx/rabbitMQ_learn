package rabbitcore.rabbit.rabbit_client.Consumer;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 一杯咖啡
 * @desc 消息消费者
 * @createTime 2018-12-15-0:44
 */
public class Myconsumer {
    private static final String EXCHANGE_NAME = "simple_exchange";
    private static final String QUEUE_NAME = "simple_queue";
    private static final String IP_ADDRESS = "ycrabbitmq.dc.zz";
    private static Integer PORT = 5672;
    private static final Logger LOG = Logger.getLogger(Myconsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        //建立连接信息
        Address[] address = new Address[]{new Address(IP_ADDRESS, PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("yunce");
        Channel channel = null;
        Connection connection = null;
        try {
            connection = factory.newConnection(address);
            channel = connection.createChannel();
            //判断信道时候就绪,未就绪会抛出异常
            channel.exchangeDeclarePassive(EXCHANGE_NAME);
            channel.exchangeDeclarePassive(EXCHANGE_NAME + "_1");
            //设置该消费者获取消息，最大unack 数量。。超过该数值，将无法继续获取消息
            channel.basicQos(5);
            //自动确认消息已被消费 autoACK = false
            // channel.basicConsume(QUEUE_NAME, false, "Myconsumer", true, false, null, new ConsumerObj(channel));
//            for (int i = 0; i < 20; i++) {
            while (true) {
                GetResponse message = channel.basicGet(QUEUE_NAME, false);
                GetResponse message2 = channel.basicGet(QUEUE_NAME + "_1", false);
                if (message != null) {
                    LOG.info("\n1 .message == " + new String(message.getBody())
                            + "\n ------------------  ");
                }
                if (message2 != null) {
                    LOG.info("2.message2 == " + new String(message2.getBody()));
                }
                //mutiple = true 确定此条消息前所有消息。
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
//                channel.basicAck(message2.getEnvelope().getDeliveryTag(), false);
//                TimeUnit.SECONDS.sleep(1);
            }
            //运行basicconsume 后直接关闭连接。会直接关闭消息获取。
//            LOG.info("消息未获取到 或者已到最大获取数");

        } catch (IOException e) {
            e.printStackTrace();
            LOG.info("交换机不存在");
        } catch (TimeoutException e) {
            e.printStackTrace();
            LOG.info("shudown2");
        } finally {
            channel.close();
            connection.close();
        }
    }
}
