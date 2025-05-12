package pro.shushi.pamirs.meta.api.core.orm.serialize;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;

/**
 * 序列化器接口
 *
 * @param <T> 入参类型
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 */
public interface Serializer<T, S> extends CommonApi {

    /**
     * 序列化
     *
     * @param ltype 对象类型，如果T为泛型，则ltype为范型实参
     * @param value 对象值
     * @return 序列化结果
     */
    Object serialize(String ltype, T value);

    /**
     * 反序列化
     *
     * @param value 序列化值
     * @return 反序列化结果
     */
    T deserialize(String ltype, String ltypeT, S value, String format);

    /**
     * 序列化方式
     *
     * @return 序列化方式
     * @see SerializeEnum
     */
    String type();

}
