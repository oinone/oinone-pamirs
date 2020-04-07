package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * id为主键的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.IdModel")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "带id模型抽象基类", summary = "带id模型抽象基类")
public abstract class IdModel extends BaseModel {

    @Base
    @Field.Integer(sequence = "AUTO_INCREMENT", M = 20)
    @Field.Advanced(priority = 5)
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true)
    protected Long id;

}
