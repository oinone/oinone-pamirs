package pro.shushi.pamirs.meta.api.core.systems.enmu;

import pro.shushi.pamirs.meta.api.CommonApi;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 枚举系统
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 *
 * @param <T> 数据字典模型类
 * @param <D> 数据字典项模型类
 *
 */
public interface EnumProcessor<T, D> extends CommonApi {

    /**
     * 从枚举类获取数据字典编码
     *
     * @param enumClass
     * @return
     */
    String fetchDictionaryFromClass(Class enumClass);

    /**
     * 从字段获取数据字典编码
     *
     * @param field 字段
     * @return
     */
    String fetchDictionaryFromField(Field field);

    /**
     * 从枚举类获取数据字典
     *
     * @param module 模块
     * @param enumClass 枚举类
     * @return
     */
    T fetchDataDictionaryFromEnum(String module, Class enumClass);

    /**
     * 从枚举类获取信息填充数据字典
     *
     * @param dataDictionary 数据字典
     * @param module 模块
     * @param enumClass 枚举类
     * @return
     */
    T fillDataDictionaryFromEnum(T dataDictionary, String module, Class enumClass);

    /**
     * 从枚举类型字段获取数据字典项列表
     *
     * @param enumClass 枚举类
     * @return
     */
    List<D> fetchEnumValues(Class enumClass);

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
    String fetchEnumValueTtype(Class enumClazz);

}
