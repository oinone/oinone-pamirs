package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * id为主键且带code、version的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.VersionModel")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "带code和version模型抽象基类", summary = "带code和version模型抽象基类")
public abstract class VersionModel extends CodeModel {

    @Base
    @Field.Integer
    @Field(displayName = "乐观锁", defaultValue = "0", required = true)
    private Long version;

}
