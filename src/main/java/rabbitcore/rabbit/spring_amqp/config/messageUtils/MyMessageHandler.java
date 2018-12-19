package rabbitcore.rabbit.spring_amqp.config.messageUtils;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author 一杯咖啡
 * @desc 消息适配器处理方法类
 * @createTime 2018-12-19-11:08
 */
public class MyMessageHandler {
    private static final Logger LOG = Logger.getLogger(MyMessageHandler.class);

    public void handleMessage(byte[] message) {
        LOG.info("-------------handleMessage byte[]------------\n" + new String(message));
    }

    public void onMessage(byte[] message) {
        LOG.info("-------------onMessage byte[]------------\n" + new String(message));
    }

    public void onMessage(String message) {
        LOG.info("-------------onMessage string ------------\n" + message);
    }

    public void onMessage(Map message) {
        LOG.info("-------------onMessage map------------\n" + message.toString());
    }
}
