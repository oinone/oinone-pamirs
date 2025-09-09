package pro.shushi.pamirs.boot.base.tmodel;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组信息")
@Model.model(GroupInfo.MODEL_MODEL)
public class GroupInfo<T> extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupInfo";

    @Field(displayName = "是否为数据节点")
    private Boolean isLeaf;

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "当前分组下数据总数")
    private Long dataCount;

    @Field(displayName = "当前分组数据统计值", summary = "转换成Json字符串")
    private String dataStatisticStr;

    @Field(displayName = "当前分组值", summary = "转换成字符串")
    private String valueStr;

    @Field(displayName = "当前分组数据", summary = "转换成Json字符串")
    private String dataListStr;

    @Field(displayName = "下一级分组信息")
    private List<GroupInfo<T>> groups;

    /**
     * 当前分组数据统计值
     */
    private Map<String, Object> dataStatistic;

    /**
     * 当前分组的值
     */
    private Object value;

    /**
     * 当前分组的数据
     */
    private List<T> dataList;

    /**
     * 当前分组数据
     */
    private T groupData;

    /**
     * 当前分组信息所依赖的分组字段信息
     */
    private GroupField groupField;

    /**
     * 分组路径
     */
    private GroupPath<T> groupPath;

    public static String stringifyValue(ModelFieldConfig fieldConfig, Object value) {
        if (value == null) {
            return null;
        }
        if (TtypeEnum.isDateType(fieldConfig.getTtype())) {
            if (value instanceof Date) {
                return DateUtils.formatDate(new Date(((Date) value).getTime()), fieldConfig.getFormat());
            } else {
                if (TtypeEnum.YEAR.value().equals(fieldConfig.getTtype())) {
                    return value.toString();
                } else {
                    return DateUtils.formatDate(new Date((long) value), fieldConfig.getFormat());
                }
            }
        } else if (value instanceof BaseEnum) {
            return ((BaseEnum<?, ?>) value).name();
        } else if (value instanceof IEnum) {
            return ((IEnum<?>) value).name();
        }
        return value.toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object valueFromString(ModelFieldConfig fieldConfig, String valueStr) {
        if (valueStr == null) {
            return null;
        }
        Class<?> valueClass;
        try {
            valueClass = Class.forName(fieldConfig.getLtype());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (TtypeEnum.isDateType(fieldConfig.getTtype())) {
            if (Date.class.isAssignableFrom(valueClass)) {
                Date date = DateUtils.formatDate(valueStr, fieldConfig.getFormat());
                if (java.sql.Date.class.isAssignableFrom(valueClass)) {
                    return new java.sql.Date(date.getTime());
                } else if (java.sql.Timestamp.class.isAssignableFrom(valueClass)) {
                    return new java.sql.Timestamp(date.getTime());
                }
                return date;
            } else {
                if (TtypeEnum.YEAR.value().equals(fieldConfig.getTtype())) {
                    return Long.parseLong(valueStr);
                } else {
                    return DateUtils.formatDate(valueStr, fieldConfig.getFormat()).getTime();
                }
            }
        } else if (Number.class.isAssignableFrom(valueClass)) {
            if (valueClass == Integer.class) {
                return Integer.valueOf(valueStr);
            } else if (valueClass == Long.class) {
                return Long.valueOf(valueStr);
            } else if (valueClass == Double.class) {
                return Double.valueOf(valueStr);
            } else if (valueClass == Float.class) {
                return Float.valueOf(valueStr);
            } else if (valueClass == Short.class) {
                return Short.valueOf(valueStr);
            } else if (valueClass == Byte.class) {
                return Byte.valueOf(valueStr);
            } else if (valueClass == java.math.BigDecimal.class) {
                return new BigDecimal(valueStr);
            } else if (valueClass == java.math.BigInteger.class) {
                return new BigInteger(valueStr);
            }
        } else if (Boolean.class.getName().equals(fieldConfig.getLtype())) {
            return Boolean.valueOf(valueStr);
        } else if (Character.class.getName().equals(fieldConfig.getLtype())) {
            if (StringUtils.isBlank(valueStr)) {
                return null;
            } else {
                return valueStr.charAt(0);
            }
        } else if (BaseEnum.class.isAssignableFrom(valueClass)) {
            return BaseEnum.getEnum((Class<? extends BaseEnum<?, ?>>) valueClass, valueStr);
        } else if (IEnum.class.isAssignableFrom(valueClass)) {
            return Enum.valueOf((Class<? extends Enum>) valueClass, valueStr);
        }
        return valueStr;
    }

}
