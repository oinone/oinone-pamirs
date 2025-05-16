package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * id为主键且带code、version的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(VersionCodeModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 39)
@Model(displayName = "带编码和乐观锁的基础模型", summary = "带编码和乐观锁的基础模型")
public abstract class VersionCodeModel extends CodeModel {

    private static final long serialVersionUID = -4551893842674122469L;

    public static final String MODEL_MODEL = "base.VersionCodeModel";

    @Base
    @Field.Version
    @Field.Integer
    @Field.Advanced(columnDefinition = "bigint(20) DEFAULT '0'")
    @Field(displayName = "乐观锁", defaultValue = "0", required = true, invisible = true)
    private Long optVersion;

}
