package pro.shushi.pamirs.meta.annotation;

import org.springframework.core.annotation.AliasFor;
import pro.shushi.pamirs.meta.base.Empty;
import pro.shushi.pamirs.meta.common.constants.MetaValueConstants;
import pro.shushi.pamirs.meta.enmu.*;

import java.lang.annotation.*;

/**
 * 属性
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    // 属性的展示描述
    @AliasFor("displayName")
    java.lang.String value() default "";

    // 属性的展示描述
    @AliasFor("value")
    java.lang.String displayName() default "";

    // 属性的描述
    java.lang.String summary() default "";

    // 是否存储
    NullableBoolEnum store() default NullableBoolEnum.NULL;

    // 是否是多值字段
    boolean multi() default false;

    // 数据库字段优先级
    long priority() default -1;

    // 后端序列化函数 SerializeEnum 或者 自定义序列化函数
    java.lang.String serialize() default serialize.NON;

    // 前端序列化函数 SerializeEnum 或者 自定义序列化函数
    java.lang.String requestSerialize() default serialize.NON;

    interface serialize {
        java.lang.String NON = "NON";
        java.lang.String JSON = "JSON";
        java.lang.String XML = "XML";
        java.lang.String COMMA = "COMMA";
        java.lang.String DOT = "DOT";
    }

    // 默认值
    java.lang.String defaultValue() default "";

    // 计算函数（函数编码）
    java.lang.String compute() default "";

    // 必填
    boolean required() default false;

    // 不可见
    boolean invisible() default false;

    // 不可变更
    boolean immutable() default false;

    // 唯一索引
    boolean unique() default false;

    // 是否可索引，如果配置该属性为真，说明需要为该字段增加数据表列索引
    boolean index() default false;

    // 国际化，是否需要翻译
    boolean translate() default false;

    // 字段追踪
    FieldTrackEnum track() default FieldTrackEnum.NON;

    // 更多配置
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Advanced {

        // api名称
        java.lang.String name() default "";

        // 数据表字段名
        java.lang.String column() default "";

        // 数据库字段类型，如果需要自定义不在ttype对应数据库字段类型列表中的类型请使用此字段填写完整数据库字段定义
        java.lang.String columnDefinition() default "";

        // 持久层查询直接返回列名，不做属性名映射
        boolean onlyColumn() default true;

        // 免权限控制
        java.lang.String[] sudo() default "";

        // 是否可被拷贝
        boolean copied() default true;

        // 支持客户端
        boolean supportClient() default true;

        // 新增字段验证过滤策略
        FieldStrategyEnum insertStrategy() default FieldStrategyEnum.DEFAULT;

        // 批量新增字段验证过滤策略
        FieldStrategyEnum batchStrategy() default FieldStrategyEnum.NOT_CHANGE;

        // 更新字段验证过滤策略
        FieldStrategyEnum updateStrategy() default FieldStrategyEnum.DEFAULT;

        // 查询字段验证过滤策略
        FieldStrategyEnum whereStrategy() default FieldStrategyEnum.DEFAULT;

        java.lang.String whereCondition() default "%s = #{%s}";

        CharsetEnum charset() default CharsetEnum.DEFAULT;

        CollationEnum collate() default CollationEnum.DEFAULT;

    }

    // 主键
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface PrimaryKey {
        // 排序
        int value() default 0;

        // id生成策略
        KeyGeneratorEnum keyGenerator() default KeyGeneratorEnum.NON;

    }

    // 乐观锁
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Version {

    }

    // 编码生成配置
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Sequence {

        java.lang.String sequence();

        java.lang.String prefix() default "";

        java.lang.String suffix() default "";

        int size() default 16;

        int step() default 1;

        boolean isRandomStep() default false;

        long initial() default 1000L;

        java.lang.String format() default "";

        SystemSourceEnum source() default SystemSourceEnum.MANUAL;

        TimePeriodEnum zeroingPeriod() default TimePeriodEnum.YEAR;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface field {

        // 字段编码
        java.lang.String value();

        java.lang.String POSITIVE_INFINITY = "Infinity";

        java.lang.String NEGATIVE_INFINITY = "-Infinity";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Binary {

        // 类型
        BinaryTypeEnum type() default BinaryTypeEnum.FILE;

        // 媒体类型
        MimeTypeEnum mime();

        // 最小值
        java.lang.String min() default "";

        // 最大值
        java.lang.String max() default "65000";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Integer {

        // 标度，数字最大位数，maximum
        int M() default 20;

        // 最小值
        java.lang.String min() default field.NEGATIVE_INFINITY;

        // 最大值
        java.lang.String max() default field.POSITIVE_INFINITY;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Float {

        // 标度，数字最大位数，maximum
        int M() default 15;

        // 精度，小数位数，decimal
        int D() default -1;

        // 最小值
        java.lang.String min() default field.NEGATIVE_INFINITY;

        // 最大值
        java.lang.String max() default field.POSITIVE_INFINITY;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Boolean {

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface String {

        // 字符串长度，单值默认128，多值默认512
        int size() default -1;

        // 最小长度
        java.lang.String min() default "";

        // 最大长度
        java.lang.String max() default "";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Text {

        // 最小长度
        java.lang.String min() default "";

        // 最大长度
        java.lang.String max() default "";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Date {

        // 时间类型
        DateTypeEnum type() default DateTypeEnum.DATETIME;

        // 时间格式
        DateFormatEnum format() default DateFormatEnum.DATETIME;

        // 时间精度
        int fraction() default 0;

        // 最小值
        java.lang.String min() default "";

        // 最大值
        java.lang.String max() default "";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Money {

        // 标度，数字最大位数，maximum
        int M() default 65;

        // 精度，小数位数，decimal
        int D() default 6;

        // 币种字段
        java.lang.String currency() default "";

        // 最小值
        java.lang.String min() default field.NEGATIVE_INFINITY;

        // 最大值
        java.lang.String max() default field.POSITIVE_INFINITY;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Html {
        // 存储字符长度
        int size() default 1024;

        // 最小长度
        java.lang.String min() default "";

        // 最大长度
        java.lang.String max() default "";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Enum {

        // 数据字典编码
        java.lang.String dictionary() default "";

        // 存储字符长度
        int size() default 128;

        // 枚举选择数量限制
        int limit() default -1;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Related {

        @AliasFor("related")
        java.lang.String[] value() default "";

        @AliasFor("value")
        // 引用字段，配合relation使用，关联模型的字段的点表达式
        java.lang.String[] related() default "";

        /**
         * This represents code that the pamirs project considers internal code that MAY not be stable within
         * major releases.
         * <p>
         * In general unnecessary changes will be avoided but you should not depend on internal classes being stable
         */
        @Target({ElementType.FIELD})
        @Retention(RetentionPolicy.RUNTIME)
        @interface Internal {

            boolean store() default true;

        }
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Relation {

        boolean store() default true;

        // 自身模型的关系字段，外键，用于取值作为查询条件查询关联模型，与referenceField的字段一一对应，如果relationField与referenceField完全一致，可缺省
        java.lang.String[] relationFields() default {};

        // 关联模型，low code模型没有class可以填此项
        java.lang.String references() default "";

        // 关联模型class，java模型有class可以填此项
        Class<?> referenceClass() default Empty.class;

        // 关联模型的关联字段，关联模型的唯一索引
        java.lang.String[] referenceFields() default {};

        // 模型筛选可选项每页个数
        int domainSize() default 15;

        // 模型筛选，数据查询过滤条件
        java.lang.String domain() default "";

        // 上下文，查询时前端传入，JSON字符串
        java.lang.String context() default "";

        // 搜索函数（函数编码）
        java.lang.String search() default "";

        // 序列化存储时的存储长度
        int columnSize() default 1024;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface one2one {

        // 更新关联操作
        OnCascadeEnum onUpdate() default OnCascadeEnum.SET_NULL;

        // 删除关联操作
        OnCascadeEnum onDelete() default OnCascadeEnum.SET_NULL;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface one2many {

        // 关系数量限制
        int limit() default -1;

        // 查询每页个数
        long pageSize() default MetaValueConstants.pageSize;

        // 排序
        java.lang.String ordering() default "";

        // 反向关联，关联关系存储在一对多关系"一"这一端
        boolean inverse() default false;

        // 更新关联操作
        OnCascadeEnum onUpdate() default OnCascadeEnum.SET_NULL;

        // 删除关联操作
        OnCascadeEnum onDelete() default OnCascadeEnum.SET_NULL;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface many2one {

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface many2many {

        // 中间模型，low code模型没有class可以填此项
        java.lang.String through() default "";

        // 中间模型显示名称，如果默认生成的显示名称重复，可以使用该注解解决冲突
        java.lang.String throughDisplayName() default "";

        // 中间模型class，java模型有class可以填此项
        Class<?> throughClass() default Empty.class;

        // 中间模型与关系模型的关联字段
        java.lang.String[] relationFields() default {};

        // 中间模型与关联模型的关联字段
        java.lang.String[] referenceFields() default {};

        // 关系数量限制
        int limit() default -1;

        // 查询每页个数
        long pageSize() default MetaValueConstants.pageSize;

        // 排序
        java.lang.String ordering() default "";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Page {

        // 是否分页
        boolean value() default true;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Override {

        // 重写继承字段（关联关系字段）
        java.lang.String value();

    }

}
