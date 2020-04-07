package pro.shushi.pamirs.meta.api.core.serialize;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;

/**
 * 序列化接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 *
 * @param <T> 入参类型
 *
 */
public interface SerializeProcessor<T> extends CommonApi {

    /**
     * 序列化
     *
     * @param serializeType 序列化器类型
     * @param ltype 对象类型，如果T为泛型，则ltype为范型实参
     * @param value 对象值
     * @return
     */
    String format(String serializeType, String ltype, T value);

    /**
     * 反序列化
     *
     * @param serializeType 序列化器类型
     * @param ltype 对象类型，如果T为泛型，则ltype为范型实参
     * @param value 序列化值
     * @param format 格式，若ltype为时间类型，则为时间格式化格式
     * @return
     */
    T parse(String serializeType, String ltype, String value, String format);

}
