package pro.shushi.pamirs.meta.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Java字段工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 2:20 下午
 */
public class EnumUtils {

    private final static Logger logger = LoggerFactory.getLogger(EnumUtils.class);

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Serializable, E extends IEnum<T>> E valueFor(Class<E> enumerated, T value) {
        try {
            Method method = enumerated.getMethod("values");
            for (IEnum<T> item : (IEnum[]) method.invoke(null)) {
                if (item.value().equals(value)) {
                    return (E) item;
                }
            }
            return null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(enumerated.getName() + ",value:" + value, e);
            return null;
        }
    }

    public static <T extends Long, E extends IEnum<T>> List<E> bitOptions(Class<E> enumType, Long total) {
        if (null == total) {
            return null;
        }
        List<E> options = new ArrayList<>();
        for (Field field : enumType.getFields()) {
            try {
                if (IEnum.class.isAssignableFrom(field.getType())) {
                    @SuppressWarnings("unchecked") E e = (E) field.get(enumType);
                    if (BitUtil.has(total, e.value())) {
                        options.add(e);
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error(enumType.getName(), e);
                return null;
            }
        }
        return options;
    }

    public static <E extends IEnum<Long>> Long sumBitValue(List<E> options) {
        if (null == options) {
            return null;
        }
        Long sum = 0L;
        for (E e : options) {
            sum += e.value();
        }
        return sum;
    }

    public static String error(ExpBaseEnum expBaseEnum) {
        return "PamirsException code: " + expBaseEnum.code() + ", type: " + expBaseEnum.type()
                + ", msg: " + expBaseEnum.msg();
    }

}
