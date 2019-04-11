package rabbitcore.rabbit.rabbit_client.producer;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 一杯咖啡
 * @desc 消息生产者
 * @createTime 2018-12-15-0:44
 */
public class MyProducer {
    private static final Logger LOG = Logger.getLogger(MyProducer.class);
    private static final String EXCHANGE_NAME = "rc_exchange";
    private static final String ROUTING_KEY = "routingkey_rc";
    private static final String QUEUE_NAME = "rc_queue";
    private static final String IP_ADDRESS = "10.253.90.20";
    private static final Integer PORT = 5672;
    private static final String VIRTUAL_Host = "yunce";
    private static SortedSet<Long> confiemSet = Collections.synchronizedSortedSet(new TreeSet<>());

    /**
     * desc: 设置消息发送到brok 失败，返回
     **/
    public static void setReturnMessage(Channel channel) {
        //创建 消息发送失败自动返回消息内容监听  manadatory = true
        channel.addReturnListener((i1, s, s1, s2, basicProperties, bytes) -> {
            LOG.info("\nreply code=" + i1
                    + "\nreplytxt=" + s1
                    + "\nexchange=" + s2
                    + "\nbasicproprtties=" + basicProperties);
            String message1 = new String(bytes);
            LOG.info("return message = " + message1);
        });
    }

    /**
     * desc: 设置确认消息发送到broker
     **/
    public static void setAckSet(Channel channel) {
        try {
            channel.confirmSelect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
                LOG.info("发送成功 ：deliverTag == " + deliveryTag + " ;; and multiple == " + multiple);
                if (multiple) {
                    LOG.info("批量成功处理");
                    confiemSet.headSet(deliveryTag - 1).clear();
                } else {
                    LOG.info("单个成功处理");
                    confiemSet.remove(deliveryTag);
                }
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
                LOG.info("发送失败：deliverTag == " + deliveryTag + " ;; and multiple =" + multiple);
                if (multiple) {
                    LOG.info("多个失败批量处理");
                    confiemSet.headSet(deliveryTag - 1).clear();
                } else {
                    LOG.info("单个失败处理");
                    confiemSet.remove(deliveryTag);
                }
            }
        });
    }

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            // uri格式: amqp:username:password@ipAddress:portNumber/virtualHost
            LOG.info("amqp://admin:admin@+" + IP_ADDRESS + ":" + PORT + "/" + VIRTUAL_Host);
            factory.setUri("amqp://admin:admin@" + IP_ADDRESS + ":" + PORT + "/" + VIRTUAL_Host);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        Connection connection;

        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            Map<String, Object> arg = new HashMap();
            arg.put("alternate-exchange", "MyAe");
            //先删除已有的信道，队列
            channel.exchangeDelete("MyAe");
            channel.queueDelete("Queue_l");

           /* channel.exchangeDeclare("MyAe", "fanout", true, false, null);
            channel.queueDeclare("Queue_l", true, false, false, null);
            channel.queueBind("Queue_l", "MyAe", "");*/

            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, arg);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            //设置确认返回
            MyProducer.setAckSet(channel);
            int count = 0;
//            while (count < 20) {
            //设置消息发送失败返回
            while (true) {
                MyProducer.setReturnMessage(channel);
                long nextSeqNo = channel.getNextPublishSeqNo();
                //设置消息properties
                Map<String, Object> headers = new HashMap<>();
                headers.put("author", "zxx");
                headers.put("email", "1139835238@qq.com");

                AMQP.BasicProperties mybasicProperties = new AMQP.BasicProperties().builder().deliveryMode(2) // 传送方式
                        .contentEncoding("UTF-8") // 编码方式
                        //.expiration("20000") // 过期时间
                        .headers(headers) //自定义属性
                        .timestamp(new Date())
                        .build();
                LOG.info("properties == " + mybasicProperties);
                try {
                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, mybasicProperties, ("right_rountingkey_test_message").getBytes());
                    count++;
                } catch (Exception e) {
                    LOG.info("发送结果 成功delivery ：" + confiemSet);
                    break;
                }
                //channel.basicPublish(EXCHANGE_NAME, "", true, mybasicProperties, ("wrong_rountingkey_test_message" + i).getBytes());
                TimeUnit.SECONDS.sleep(1);
                //消息编号
                confiemSet.add(nextSeqNo);
            }
            LOG.info("发送结果 成功delivery ：" + confiemSet);
           /* //删除信道，队列 返回确定值
            AMQP.Exchange.DeleteOk exchangeIndex = channel.exchangeDelete(EXCHANGE_NAME,true);
            AMQP.Queue.DeleteOk queueIndex = channel.queueDelete(QUEUE_NAME,true,true);
            LOG.info("exchangeIndex:"+exchangeIndex.toString());
            LOG.info("queueIndex:"+queueIndex.toString());*/
            //关闭信道，连接
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
