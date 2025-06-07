package pro.shushi.pamirs.eip.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;


@Base
@Model.model(EipContextVariable.MODEL_MODEL)
@Model(displayName = "Map映射参数定义", labelFields = {"key", "value"})
public class EipContextVariable extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.eip.tmodel.EipContextVariable";

    @Field.String
    @Field(displayName = "键")
    private String key;

    @Field.String
    @Field(displayName = "值")
    private String value;


}
