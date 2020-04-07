package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 无id关系模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.BaseRelation")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "关系模型抽象基类", summary = "关系模型抽象基类")
public abstract class BaseRelation extends BaseModel {

}
