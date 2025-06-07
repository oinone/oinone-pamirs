package pro.shushi.pamirs.framework.connectors.event.rabbitmq.marshalling;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.messaging.MessageHeaders;
import pro.shushi.pamirs.framework.connectors.event.manager.TopicClassCacheManager;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * PamirsMessageJsonConverter
 *
 * @author yakir on 2023/12/09 13:59.
 */
public class PamirsMessageJsonConverter extends AbstractMessageConverter {

    public PamirsMessageJsonConverter() {
        this.setCreateMessageIds(true);
    }

    @Override
    public Message createMessage(Object object, MessageProperties messageProperties) {
        String json = JsonUtils.toJSONString(object);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return MessageBuilder.withBody(bytes).andProperties(messageProperties).build();
    }

    @Override
    public org.springframework.messaging.Message<? extends Serializable> fromMessage(Message message) throws MessageConversionException {
        byte[] bytes = message.getBody();
        String queue = message.getMessageProperties().getConsumerQueue();
        Serializable payload = JsonUtils.parseObject(new String(bytes, StandardCharsets.UTF_8), TopicClassCacheManager.get(queue));
        MessageProperties msgProps = message.getMessageProperties();
        String msgId = message.getMessageProperties().getMessageId();
        Map<String, Object> headerMap = new HashMap<>(msgProps.getHeaders());
        headerMap.put("msgId", msgId);
        MessageHeaders headers = new MessageHeaders(headerMap);
        return org.springframework.messaging.support.MessageBuilder.createMessage(payload, headers);
    }
}
