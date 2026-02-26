package pro.shushi.pamirs.meta.api.core.compute.systems.type;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * 类型系统
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Order
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TypeProcessor extends CommonApi {

    int DEFAULT_SHORT = 6;

    int DEFAULT_INTEGER = 11;

    int DEFAULT_BIGINT = 20;

    int DEFAULT_BIGINT_LIMIT = 20;

    int GRAPH_BIGINT_LIMIT = 15;

    int DEFAULT_FLOAT = 7;

    int DEFAULT_FLOAT_DECIMAL = 2;

    int DEFAULT_DOUBLE = 15;

    int DEFAULT_DOUBLE_DECIMAL = 4;

    int DEFAULT_DECIMAL = 65;

    int DEFAULT_DECIMAL_DECIMAL = 6;

    int DEFAULT_STRING = 128;

    int DEFAULT_EMAIL = 256;

    int DEFAULT_MULTI = 512;

    /**
     * 从ltype获取默认业务类型ttype
     *
     * @param ltype     字段Java类型
     * @param ltypeT    字段Java泛型
     * @param serialize 序列化
     * @return ttype
     */
    String defaultTtypeFromLtype(String ltype, String ltypeT, String serialize);

    /**
     * 从ttype获取数据库列类型
     *
     * @param ttype   业务类型
     * @param ltype   java类型
     * @param multi   是否多值字段
     * @param bit     是否按位求和字段
     * @param size    长度限制
     * @param decimal 有效小数位数
     * @return columnType
     */
    String defaultColumnTypeFromTtype(final String ttype, final String ltype, final Boolean multi, final Boolean bit,
                                      final Integer size, final Integer decimal);

    /**
     * 获取默认有效小数位数
     *
     * @param size    长度限制
     * @param decimal 有效小数位数
     * @return 有效小数位数
     */
    Integer fetchDefaultDecimal(Integer size, Integer decimal);

    /**
     * 根据类型获取默认size
     *
     * @param ttype 业务类型
     * @param ltype java类型
     * @param multi 是否多值
     * @return 长度限制
     */
    Integer fetchDefaultSize(final TtypeEnum ttype, final String ltype, Boolean multi);

    /**
     * 获取浮点数默认size
     *
     * @param ltype java类型
     * @param size  长度限制
     * @return 长度限制
     */
    Integer fetchDefaultSizeForFloat(String ltype, Integer size);

    /**
     * 获取整数默认size
     *
     * @param ltype java类型
     * @param size  长度限制
     * @return 长度限制
     */
    Integer fetchDefaultSizeForInteger(String ltype, Integer size);

    /**
     * 获取列定义
     *
     * @param columnType   列类型
     * @param nullable     是否可以为空
     * @param defaultValue 默认值
     * @param extra        额外信息
     * @return columnDefinition
     */
    String columnDefinition(String columnType, Boolean nullable, String defaultValue, String extra);

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
     * 是否是引用关联关系字段
     *
     * @param ttype 业务类型
     * @return 是否是关联关系字段
     */
    @SuppressWarnings("unused")
    boolean isRelationRelatedField(String ttype, String relatedTtype);

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
