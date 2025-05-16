package pro.shushi.pamirs.core.common.business.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(CurrentPartner.MODEL_MODEL)
@Model(displayName = "当前公司信息",summary = "当前公司信息")
public class CurrentPartner extends TransientModel {

    public static final String MODEL_MODEL = "core.common.CurrentPartner";

    @Field.String
    @Field(displayName = "公司编码")
    private String code;

    @Field.String
    @Field(displayName = "公司名字")
    private String name;
}
