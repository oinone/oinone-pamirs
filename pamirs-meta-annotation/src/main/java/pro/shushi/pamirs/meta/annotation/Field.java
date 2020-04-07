package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.base.Empty;
import pro.shushi.pamirs.meta.enmu.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    // 是否是多值字段
    boolean multi() default false;

    // 属性的展示描述
    java.lang.String displayName() default "";

    // 属性的描述
    java.lang.String summary() default "";

    // 是否存储
    NullableBoolEnum store() default NullableBoolEnum.NULL;

    // 序列化函数 SerializeEnum 或者 自定义序列化函数
    java.lang.String serialize() default serialize.NON;

    interface serialize {
        java.lang.String NON = "NON";
        java.lang.String JSON = "JSON";
        java.lang.String COMMA = "COMMA";
        java.lang.String DOT = "DOT";
    }

    // 默认值
    java.lang.String defaultValue() default "";

    // 计算字段
    java.lang.String compute() default "";

    // 反向计算
    java.lang.String inverse() default "";

    // 监听字段
    java.lang.String[] watch() default {};

    // 是否必填
    boolean required() default false;

    // 约束-校验函数
    java.lang.String[] check() default {};

    // 约束-校验表达式
    java.lang.String[] rule() default {};

    // 不可变更
    boolean immutable() default false;

    // 唯一索引
    boolean unique() default false;

    // 是否可索引，如果配置该属性为真，说明需要为该字段增加数据表列索引
    boolean index() default false;

    // 国际化，是否需要翻译
    boolean translate() default false;

    // 是否视图可见
    boolean invisible() default false;

    // 字段追踪
    FieldTrackEnum track() default FieldTrackEnum.NON;

    // 更多配置
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Advanced {

        // 技术名称
        java.lang.String name() default "";

        // 数据表字段名
        java.lang.String column() default "";

        // 数据库字段类型，如果需要自定义不在ttype对应数据库字段类型列表中的类型请使用此字段填写完整数据库字段定义
        java.lang.String columnDefinition() default "";

        long priority() default 100;

        // 搜索函数
        java.lang.String search() default "";

        // 复合校验函数
        java.lang.String complexCheck() default "";

        // 免权限控制
        java.lang.String[] sudo() default "";

        // 是否可被拷贝
        boolean copied() default true;

        // 是否只读
        boolean readonly() default false;

    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Sequence {

        java.lang.String sequence();

        java.lang.String prefix() default "";

        java.lang.String suffix() default "";

        java.lang.String separator() default "";

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface field {

        // 字段编码
        java.lang.String value();

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Integer {

        // 标度，数字最大位数，maximum
        short M() default 20;

        // 序列生成器
        java.lang.String sequence() default "";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Float {

        // 标度，数字最大位数，maximum
        short M() default 15;

        // 精度，小数位数，decimal
        short D() default 6;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Boolean {

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface String {

        // 字符串长度
        short size() default 0;

        // 序列生成器
        java.lang.String sequence() default "";

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Text {

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Date {

        // 时间类型
        DateTypeEnum type() default DateTypeEnum.DATETIME;

        // 时间格式
        DateFormatEnum format() default DateFormatEnum.DATETIME;

        // 时间精度
        short fraction() default 0;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Money {

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Html {

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Enum {

        // 数据字典编码
        java.lang.String dictionary() default "";

        // 存储字符长度
        int size() default 512;

        // 枚举选择数量限制
        short limit() default -1;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Related {

        // 引用字段，配合relation使用，关联模型的字段的点表达式
        java.lang.String[] related();

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
        Class referenceClass() default Empty.class;

        // 关联模型的关联字段，关联模型的唯一索引
        java.lang.String[] referenceFields() default {};

        // 模型筛选可选项每页个数
        int domainSize() default 100;

        // 模型筛选，前端查询可选项使用
        java.lang.String domain() default "";

        // 更新关联操作
        OnCascadeEnum onUpdate() default OnCascadeEnum.SET_NULL;

        // 删除关联操作
        OnCascadeEnum onDelete() default OnCascadeEnum.SET_NULL;

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface one2one {

    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface one2many {

        // 关系数量限制
        int limit() default -1;

        // 查询每页个数
        int pageSize() default 20;

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

        // 中间模型class，java模型有class可以填此项
        Class throughClass() default Empty.class;

        // 关系数量限制
        int limit() default -1;

        // 查询每页个数
        int pageSize() default 20;

    }

}
