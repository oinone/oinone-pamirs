package pro.shushi.pamirs.meta.api.dto.clazz;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.enmu.OnCascadeEnum;

/**
 * 约束
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Data
public class Constraints {

    // 是否为一对一，true为一对一，false为多对一
    private boolean unique;

    // 外键名称，即外键引用关联关系字段的名称
    private String foreignKey;

    // 自身模型的关系字段，用于取值作为查询条件查询关联模型，与referenceField的字段一一对应，如果relationField与referenceField完全一致，可缺省
    private String[] relationFields;

    // 关联模型，low code模型没有class可以填此项
    private String references;

    // 关联模型class，java模型有class可以填此项
    private Class referenceClass;

    // 关联模型的关联字段，关联模型的唯一索引
    private String[] referenceFields;

    // 关系数量限制
    private int limit;

    // 查询每页个数
    private int pageSize;

    // 模型筛选可选项每页个数
    private int domainSize;

    // 模型筛选，前端查询可选项使用
    private String domain;

    // 更新关联操作
    private OnCascadeEnum onUpdate;

    // 删除关联操作
    private OnCascadeEnum onDelete;

}
