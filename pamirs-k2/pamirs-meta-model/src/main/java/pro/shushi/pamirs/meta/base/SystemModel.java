package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 系统抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(SystemModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, name = "system", priority = 47)
@Model(displayName = "系统模型基类", summary = "系统模型基类")
public abstract class SystemModel extends K2 {

    private static final long serialVersionUID = 78502644885304491L;

    public static final String MODEL_MODEL = "system.SystemModel";

    @Base
    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    protected Long id;

}
