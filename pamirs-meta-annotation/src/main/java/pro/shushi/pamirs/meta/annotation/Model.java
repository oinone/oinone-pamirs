package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.base.Empty;
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
    String displayName() default "";

    // 主键
    String[] pk() default {};

    // 描述摘要
    String summary() default "";

    // 模型约束-校验，校验函数
    String[] check() default {};

    // 模型约束-校验，校验表达式
    String[] rule() default {};

    // 数据标题, 用于前端展示
    String[] labelFields() default {};

    // 进度条字段, 默认为空
    String progressField() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Advanced {

        // 技术名称
        String name() default "";

        // 是否是链式模型
        boolean chain() default true;

        // 数据库名
        String database() default "";

        // 数据表名
        String table() default "";

        // 指定数据源类型
        DataSourceEnum ds() default DataSourceEnum.MYSQL;

        // 索引/联合索引
        String[] index() default {};

        // 唯一索引
        String[] unique() default {};

        // 优先级
        long priority() default 100;

        // 可被管理，例如自动建表或更新表
        boolean managed() default true;

        // 排序
        String ordering() default "";

        // 模型类型
        ModelTypeEnum type() default ModelTypeEnum.STORE;

        // 是否是描述多对多关系的模型
        NullableBoolEnum relationship() default NullableBoolEnum.NULL;

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
    @interface Persistence {

        boolean logicDelete() default true;

        String logicDeleteColumn() default "is_deleted";

        String logicDeleteValue() default "REPLACE(unix_timestamp(NOW(6)),'.','')";

        String logicNotDeleteValue() default "0";

        boolean optimisticLocker() default false;

        String optimisticLockerColumn() default "";

    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Code {

        String sequence();

        String prefix() default "";

        String suffix() default "";

        String separator() default "";

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
        Class referenceClass() default Empty.class;

        // 关联模型的关联字段，关联模型的唯一索引
        java.lang.String[] referenceFields();

        // 关系数量限制
        int limit() default -1;

        // 查询每页个数
        int pageSize() default 20;

        // 模型筛选可选项每页个数
        int domainSize() default 100;

        // 模型筛选，前端查询可选项使用
        java.lang.String domain() default "";

        // 更新关联操作
        OnCascadeEnum onUpdate() default OnCascadeEnum.SET_NULL;

        // 删除关联操作
        OnCascadeEnum onDelete() default OnCascadeEnum.SET_NULL;

    }

}
