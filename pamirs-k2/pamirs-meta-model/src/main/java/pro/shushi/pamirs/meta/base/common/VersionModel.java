package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * id为主键且带乐观锁的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(VersionModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 38)
@Model(displayName = "乐观锁基础模型", summary = "乐观锁基础模型")
public abstract class VersionModel extends IdModel {

    private static final long serialVersionUID = 7531531743548130716L;

    public static final String MODEL_MODEL = "base.VersionModel";

    @Base
    @Field.Version
    @Field.Integer
    @Field.Advanced(columnDefinition = "bigint(20) DEFAULT '0'")
    @Field(displayName = "乐观锁", defaultValue = "0", required = true, invisible = true)
    private Long optVersion;

}
