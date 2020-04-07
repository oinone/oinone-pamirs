package pro.shushi.pamirs.meta.api.core.systems.type;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * 类型系统
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
public interface TypeProcessor extends CommonApi {

    /**
     * 从ltype获取默认业务类型ttype
     *
     * @param ltype 字段Java类型
     * @param ltypeT 字段Java泛型
     * @return ttype
     */
    String defaultTtypeFromLtype(String ltype, String ltypeT);

    /**
     * 从ttype获取数据库列类型
     *
     * @param ttype 业务类型
     * @param ltype java类型
     * @param multi 是否多值字段
     * @param size 长度限制
     * @param decimal 有效小数位数
     * @return columnType
     */
    String defaultColumnTypeFromTtype(String ttype, String ltype, Boolean multi, Integer size, Short decimal);

    /**
     * 获取列定义
     *
     * @param columnType 列类型
     * @param nullable 是否可以为空
     * @param defaultValue 默认值
     * @param extra 额外信息
     * @return columnDefinition
     */
    String defaultColumnDefinition(String columnType, Boolean nullable, String defaultValue, String extra);

    /**
     * 前端类型获取
     *
     * @param ttype 业务类型
     * @param size 长度限制
     * @return numberGraphType
     */
    String fetchNumberGraphType(String ttype, Integer size);

    /**
     * 是否是关联关系字段
     *
     * @param ttype 业务类型
     * @return 是否是关联关系字段
     */
    boolean isRelationField(String ttype);

    /**
     * 是否是引用字段
     *
     * @param ttype 业务类型
     * @return 是否是关联关系字段
     */
    @SuppressWarnings("unused")
    boolean isRelatedField(String ttype);

    /**
     * 是否是枚举类型
     *
     * @param ttype 业务类型
     * @return 是否是枚举字段
     */
    boolean isEnumField(String ttype);

    /**
     * 是否基本类型
     *
     * @param ttype 业务类型
     * @return 是否是基本类型
     */
    @SuppressWarnings("unused")
    boolean isBasicField(String ttype);

}
