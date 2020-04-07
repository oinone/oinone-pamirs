package pro.shushi.pamirs.meta.api.dto.clazz;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.enmu.*;

import java.lang.reflect.Field;

/**
 * 字段
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Data
public class Fields {

    private Field javaField;

    // 是否是多值字段
    private boolean multi;

    // 属性的展示描述
    private String displayName;

    // 属性的描述
    private String summary;

    // 是否存储
    private NullableBoolEnum store;

    // 序列化函数 SerializeEnum 或者 自定义序列化函数
    private String serialize;

    // 默认值
    private String defaultValue;

    // 默认值函数
    private String defaultCompute;

    // 计算字段
    private String compute;

    // 反向计算
    private String inverse;

    // 监听字段
    private String[] watch;

    // 是否必填
    boolean required;

    // 约束-校验函数
    private String[] check;

    // 约束-校验表达式
    private String[] rule;

    // 不可变更
    private boolean immutable;

    // 唯一索引
    private boolean unique;

    // 是否可索引，如果配置该属性为真，说明需要为该字段增加数据表列索引
    private boolean index;

    // 国际化，是否需要翻译
    private boolean translate;

    // 是否视图可见
    private boolean invisible;

    // 字段追踪
    private FieldTrackEnum track;

    // @interface Advanced

    // 技术名称
    private String name;

    // 数据表字段名
    private String column;

    // 数据库字段类型，如果需要自定义不在ttype对应数据库字段类型列表中的类型请使用此字段填写完整数据库字段定义
    private String columnDefinition;

    private long priority;

    // 搜索函数
    private String search;

    // 复合校验函数
    private String complexCheck;

    // 免权限控制
    private String[] sudo;

    // 是否可被拷贝
    private boolean copied;

    // 是否只读
    private boolean readonly;

    //@interface field

    // 字段编码
    private String field;

    // @interface typed

    // 标度，数字最大位数，maximum
    private short M;

    // 精度，小数位数，decimal
    private short D;

    // 字符串长度
    private short size;

    // 序列生成器
    private String sequence;

    // 时间类型
    private DateTypeEnum type;

    // 时间格式
    private DateFormatEnum format;

    // 时间精度
    private short fraction;

    // 数据字典编码
    private String dictionary;

    // 枚举选择数量限制
    private short limit;

    // 引用字段，配合relation使用，关联模型的字段的点表达式
    private String[] related;

    private boolean relationStore;

    // 自身模型的关系字段，外键，用于取值作为查询条件查询关联模型，与referenceField的字段一一对应，如果relationField与referenceField完全一致，可缺省
    private String[] relationFields;

    // 关联模型，low code模型没有class可以填此项
    private String references;

    // 关联模型class，java模型有class可以填此项
    private Class referenceClass;

    // 关联模型的关联字段，关联模型的唯一索引
    private String[] referenceFields;

    // 模型筛选可选项每页个数
    private int domainSize;

    // 模型筛选，前端查询可选项使用
    private String domain;

    // 更新关联操作
    private OnCascadeEnum onUpdate;

    // 删除关联操作
    private OnCascadeEnum onDelete;

    // 查询每页个数
    private int pageSize;

    // 中间模型，low code模型没有class可以填此项
    private String through;

    // 中间模型class，java模型有class可以填此项
    private Class throughClass;

}
