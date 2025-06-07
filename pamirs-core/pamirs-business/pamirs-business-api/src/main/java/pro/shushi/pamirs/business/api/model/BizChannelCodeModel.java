package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

@Base
@Model.model(BizChannelCodeModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "带渠道、code的模型业务抽象基类", summary = "带渠道、code模型业务抽象基类")
public class BizChannelCodeModel extends BizCodeModel {

    public static final String MODEL_MODEL = "business.BizChannelCodeModel";

    @Base
    @Field.String(size = 50)
    @Field(displayName = "业务渠道")
    private String bizChannel;
}
