package pro.shushi.pamirs.meta.api.dto.clazz;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * 模型类定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Data
public class Clazz {

    private Class<?> javaClazz;

    // @Model

    // 显示名称
    private String displayName;

    // 模型编码
    private String model;

    // 主键
    private String[] pk;

    // 描述摘要
    private String summary;

    // 模型约束-校验，校验函数
    private String[] check;

    // 模型约束-校验，校验表达式
    private String[] rule;

    // 数据标题, 用于前端展示
    private String[] labelFields;

    // 进度条字段, 默认为空
    private String progressField;

    // @Model.Advanced

    // 技术名称
    private String name;

    // 是否是链式模型
    private boolean chain;

    // 数据库名
    private String database;

    // 数据表名
    private String table;

    // 指定数据源类型
    private DataSourceEnum ds;

    // 索引/联合索引
    private String[] index;

    // 唯一索引
    private String[] unique;

    // 优先级
    private long priority;

    // 可被管理，例如自动建表或更新表
    private boolean managed;

    // 排序
    private String ordering;

    // 模型类型
    private ModelTypeEnum type;

    // 是否是描述多对多关系的模型
    private NullableBoolEnum relationship;

    // 继承
    private String[] inherited;

    // 继承类
    private Class[] inheritedClass;

    // 不从父类继承的字段
    private String[] unInheritedFields;

    // 不从父类继承的函数
    private String[] unInheritedFunctions;

    // 是否系统字段
    private Boolean base;

    // 约束
    private List<Constraints> constraintList;

    // 字段
    private List<Fields> fieldsList;

}
