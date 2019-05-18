package rabbitcore.rabbit.rabbit_client.Consumer;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-16-22:08
 */
public class ConsumerObj implements Consumer {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerObj.class);
    private Integer counter = 0;
    private Channel channel;

    public ConsumerObj(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        LOG.info("获取消费者 方法handleConsumeOk调用;consumerTag == " + consumerTag);
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        LOG.info("handleCancelOk 方法被调用;consumerTag == " + consumerTag);
    }

    @Override
    public void handleCancel(String consumerTag) {
        LOG.info("handleCancel 方法被调用;consumerTag == " + consumerTag);
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        LOG.info("handleShutdownSignal 方法被调用;consumerTag == " + consumerTag);
        LOG.info(" sig == " + sig);
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        LOG.info("handleRecoverOk 方法被调用;consumerTag == " + consumerTag);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        LOG.info("consumerTag == " + consumerTag
                + "\nenvelope == " + envelope.toString()
                + "\nproperties == " + properties
                + "\nmessage body == " + new String(body));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (counter % 2 == 0) {
            channel.basicAck(envelope.getDeliveryTag(), false);
            LOG.info("checked ACK !!!! and counter == " + counter);
        } else {
            LOG.info("Unchecked ACK!!!! and counter == " + counter);
        }
        if (counter == 4) {
            channel.basicCancel(consumerTag);
        }
        counter++;
        LOG.info("counter == " + counter);
    }
}
