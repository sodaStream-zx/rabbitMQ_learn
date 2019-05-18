package rabbitcore.rabbit.spring_amqp.config.messageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-19-16:11
 */
public class MyMessageConvertor implements MessageConverter {
    private static final Logger LOG = LoggerFactory.getLogger(MyMessageHandler.class);

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        LOG.info("-------------toMessage ------------\n" + o.toString());
        return new Message(o.toString().getBytes(), messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        LOG.info("-------------toMessage ------------\n" + message.toString());
        return new String(message.getBody());
    }
}
