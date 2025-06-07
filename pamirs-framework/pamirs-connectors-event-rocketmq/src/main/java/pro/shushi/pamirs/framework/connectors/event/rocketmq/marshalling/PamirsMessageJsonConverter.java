package pro.shushi.pamirs.framework.connectors.event.rocketmq.marshalling;

import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * PamirsMessageJsonConverter
 *
 * @author yakir on 2023/12/09 13:59.
 */
public class PamirsMessageJsonConverter extends AbstractMessageConverter {

    private final CompositeMessageConverter messageConverter;

    public PamirsMessageJsonConverter() {
        super(MediaType.APPLICATION_JSON);
        List<MessageConverter> messageConverters = new ArrayList<>();
        ByteArrayMessageConverter byteArrayMessageConverter = new ByteArrayMessageConverter();
        byteArrayMessageConverter.setContentTypeResolver(null);
        messageConverters.add(byteArrayMessageConverter);
        messageConverters.add(this);
        messageConverter = new CompositeMessageConverter(messageConverters);
    }

    @Override
    public boolean canConvertFrom(Message<?> message, Class<?> targetClass) {
        return supports(targetClass);
    }

    @Override
    public boolean canConvertTo(Object payload, MessageHeaders headers) {
        return supports(payload.getClass());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        Object payload = message.getPayload();
        Object obj = null;
        if (payload instanceof byte[]) {
            obj = JsonUtils.parseObject(new String((byte[]) payload, StandardCharsets.UTF_8), targetClass);
        } else if (payload instanceof String) {
            obj = JsonUtils.parseObject((String) payload, targetClass);
        }

        return obj;
    }

    @Override
    public Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        Object obj;
        if (byte[].class == getSerializedPayloadClass()) {
            if (payload instanceof String && JSON.isValid((String) payload)) {
                obj = ((String) payload).getBytes(StandardCharsets.UTF_8);
            } else {
                obj = JsonUtils.toJSONString(payload).getBytes(StandardCharsets.UTF_8);
            }
        } else {
            if (payload instanceof String && JSON.isValid((String) payload)) {
                obj = payload;
            } else {
                obj = JsonUtils.toJSONString(payload);
            }
        }

        return obj;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }
}
