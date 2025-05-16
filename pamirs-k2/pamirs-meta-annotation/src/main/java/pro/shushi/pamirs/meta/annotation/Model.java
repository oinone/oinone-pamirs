package pro.shushi.pamirs.meta.annotation;

import org.springframework.core.annotation.AliasFor;
import pro.shushi.pamirs.meta.base.Empty;
import pro.shushi.pamirs.meta.common.constants.MetaValueConstants;
import pro.shushi.pamirs.meta.common.constants.TableInfoDefaultValueConstants;
import pro.shushi.pamirs.meta.enmu.*;

import java.lang.annotation.*;

/**
 * 实体
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Model {

    // 显示名称
    @AliasFor("displayName")
    String value() default "";

    // 显示名称
    @AliasFor("value")
    String displayName() default "";

    // 简介，描述摘要
    String summary() default "";

    // 数据标题, 用于前端展示
    String[] labelFields() default {};

    // 数据标题格式, 默认为空
    String label() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Ds {
        // 数据源名
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Advanced {

        // api名称
        String name() default "";

        // 是否是链式模型
        @SuppressWarnings("unused")
        boolean chain() default true;

        // 逻辑数据表名
        String table() default "";

        // 表备注，默认取简介summary
        String remark() default "";

        // 索引/联合索引
        String[] index() default {};

        // 唯一索引
        String[] unique() default {};

        // 优先级
        long priority() default MetaValueConstants.priority;

        // 可被管理，例如自动建表或更新表
        boolean managed() default true;

        // 排序
        String ordering() default "";

        // 模型类型
        ModelTypeEnum type() default ModelTypeEnum.STORE;

        // 是否是描述多对多关系的模型
        NullableBoolEnum relationship() default NullableBoolEnum.NULL;

        // 支持客户端
        boolean supportClient() default true;

        // 继承
        String[] inherited() default {};

        // 继承类
        Class[] inheritedClass() default {};

        // 不从父类继承的字段
        String[] unInheritedFields() default {};

        // 不从父类继承的函数
        String[] unInheritedFunctions() default {};

    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface MultiTable {

        // 多表继承父模型中的类型字段编码
        String typeField() default "";

    }

    // 子模型多表继承父模型
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface MultiTableInherited {

        // 多表继承父模型中的类型字段值
        String type() default "";

        // 冗余父模型除主键值外的数据
        boolean redundancy() default true;

    }

    // 换表继承
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ChangeTableInherited {

    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Persistence {

        boolean logicDelete() default TableInfoDefaultValueConstants.DEFAULT_LOGIC_DELETE;

        String logicDeleteColumn() default TableInfoDefaultValueConstants.DEFAULT_LOGIC_DELETE_COLUMN;

        String logicDeleteValue() default TableInfoDefaultValueConstants.DEFAULT_LOGIC_DELETE_VALUE;

        String logicNotDeleteValue() default TableInfoDefaultValueConstants.DEFAULT_LOGIC_NOT_DELETE_VALUE;

        boolean underCamel() default TableInfoDefaultValueConstants.DEFAULT_UNDER_CAMEL;

        boolean capitalMode() default TableInfoDefaultValueConstants.DEFAULT_CAPITAL_MODE;

        CharsetEnum charset() default CharsetEnum.UTF8MB4;

        CollationEnum collate() default CollationEnum.BIN;

    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Code {

        String sequence();

        String prefix() default "";

        String suffix() default "";

        int size() default 16;

        int step() default 1;

        boolean isRandomStep() default false;

        long initial() default 1000L;

        String format() default "";

        SystemSourceEnum source() default SystemSourceEnum.MANUAL;

        TimePeriodEnum zeroingPeriod() default TimePeriodEnum.YEAR;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface model {

        // 模型编码，严重警告：若已安装，该值不可变更
        String value();

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Constraints {

        // 模型编码，严重警告：若已安装，该值不可变更
        Constraint[] value();

    }

    // 外键
    @Target({})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Constraint {

        // 是否为一对一，true为一对一，false为多对一
        boolean unique() default false;

        // 外键名称，即外键引用关联关系字段的名称
        java.lang.String foreignKey();

        // 自身模型的关系字段，用于取值作为查询条件查询关联模型，与referenceField的字段一一对应，如果relationField与referenceField完全一致，可缺省
        java.lang.String[] relationFields();

        // 关联模型，low code模型没有class可以填此项
        java.lang.String references() default "";

        // 关联模型class，java模型有class可以填此项
        Class<?> referenceClass() default Empty.class;

        // 关联模型的关联字段，关联模型的唯一索引
        java.lang.String[] referenceFields();

        // 关系数量限制
        int limit() default -1;

        // 查询每页个数
        long pageSize() default 20;

        // 模型筛选可选项每页个数
        int domainSize() default 100;

        // 模型筛选，前端查询可选项使用
        java.lang.String domain() default "";

        // 更新关联操作
        OnCascadeEnum onUpdate() default OnCascadeEnum.SET_NULL;

        // 删除关联操作
        OnCascadeEnum onDelete() default OnCascadeEnum.SET_NULL;

    }

    // 静态模型配置，非低代码方式
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Static {

        String module() default "";

        String moduleAbbr() default "";

        boolean onlyBasicTypeField() default true;

    }

    // 无代码模型 (低无一体)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Fuse {

    }

}
