package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enumclass.ModelTypeEnumCls;

/**
 * id为主键的关系模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.IdRelation")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "带id关系模型抽象基类", summary = "带id关系模型抽象基类")
public abstract class IdRelation extends BaseRelation {

    @Base
    @Field.Integer(sequence = "ID")
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引")
    protected Long id;

}
