package pro.shushi.pamirs.meta.util;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Java字段工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 2:20 下午
 */
@Slf4j
public class EnumUtils {

    public static <E extends IEnum, T> E valueFor(Class<E> enumerated, T value) {
        try {
            Method method = enumerated.getMethod("values");
            for(IEnum item : (IEnum[])method.invoke(null)){
                if(item.value().equals(value)){
                    return (E)item;
                }
            }
            return null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(enumerated.getName() + ",value:" + value, e);
            return null;
        }
    }

}
