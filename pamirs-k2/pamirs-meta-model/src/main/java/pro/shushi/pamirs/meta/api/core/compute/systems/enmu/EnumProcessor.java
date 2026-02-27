package pro.shushi.pamirs.meta.api.core.compute.systems.enmu;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.enmu.api.BaseEnumApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 枚举系统
 *
 * @param <T> 数据字典模型类
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 */
@Order
@SPI(factory = SpringServiceLoaderFactory.class)
public interface EnumProcessor<T> extends BaseEnumApi, CommonApi {

    /**
     * 从字段获取数据字典编码
     *
     * @param field 字段
     * @return 返回值
     */
    String fetchDictionaryFromField(Field field);

    /**
     * 从枚举类获取数据字典
     *
     * @param module    模块
     * @param enumClass 枚举类
     * @return 返回值
     */
    T fetchDataDictionaryFromEnum(String module, Class<?> enumClass);

    /**
     * 从枚举类获取信息填充数据字典
     *
     * @param dataDictionary 数据字典
     * @param module         模块
     * @param enumClass      枚举类
     * @return 返回值
     */
    T fillDataDictionaryFromEnum(T dataDictionary, String module, Class<?> enumClass);

    /**
     * 从枚举类型字段获取数据字典项列表
     *
     * @param enumClass 枚举类
     * @param <D>       数据字典项模型类
     * @return 返回值
     */
    <D> List<D> fetchEnumValues(Class<?> enumClass);

    /**
     * 获取枚举字段内部值的业务类型
     *
     * @param enumField 枚举字段
     * @return 业务类型
     */
    String fetchEnumValueTtype(Field enumField);

    /**
     * 获取枚举类内部值的业务类型
     *
     * @param enumClazz 枚举类
     * @return 业务类型
     */
    String fetchEnumValueTtype(Class<?> enumClazz);

}
