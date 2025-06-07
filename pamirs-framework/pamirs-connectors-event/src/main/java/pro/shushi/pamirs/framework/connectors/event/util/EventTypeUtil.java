package pro.shushi.pamirs.framework.connectors.event.util;

import org.springframework.aop.framework.AopProxyUtils;
import pro.shushi.pamirs.framework.connectors.event.api.NotifyConsumer;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * EventTypeUtil
 *
 * @author yakir on 2023/12/21 13:56.
 */
public class EventTypeUtil {

    public static Type getMessageType(NotifyConsumer<? extends Serializable> notifyConsumer) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(notifyConsumer);
        Type matchedGenericInterface = null;
        while (null != targetClass) {
            Type[] interfaces = targetClass.getGenericInterfaces();
            if (null != interfaces) {
                for (Type type : interfaces) {
                    if (type instanceof ParameterizedType && (Objects.equals(((ParameterizedType) type).getRawType(), NotifyConsumer.class))) {
                        matchedGenericInterface = type;
                        break;
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        if (Objects.isNull(matchedGenericInterface)) {
            return Object.class;
        }

        Type[] actualTypeArguments = ((ParameterizedType) matchedGenericInterface).getActualTypeArguments();
        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
            return actualTypeArguments[0];
        }
        return Object.class;
    }
}
