package pro.shushi.pamirs.meta.api.core.orm.serialize;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 字段序列化接口
 *
 * @param <T> 入参类型
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SerializeProcessor<T> extends CommonApi {

    /**
     * 序列化
     *
     * @param serializeType 序列化器类型
     * @param ltype         对象类型，如果T为泛型，则ltype为范型实参
     * @param value         对象值
     * @return 返回值
     */
    Object serialize(String serializeType, String ltype, T value);

    /**
     * 反序列化
     *
     * @param serializeType 序列化器类型
     * @param ltype         对象类型，如果T为泛型，则ltype为范型实参
     * @param ltypeT        对象类型泛型
     * @param format        格式，若ltype为时间类型，则为时间格式化格式
     * @param value         序列化值
     * @return 返回值
     */
    T deserialize(String serializeType, String ltype, String ltypeT, String format, String value);

}
