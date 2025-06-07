package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 调用参数
 *
 * @author Adamancy Zhang at 21:48 on 2025-02-26
 */
@Base
@Model.model(EipCallParam.MODEL_MODEL)
@Model(displayName = "调用参数", label = "activeRecord.key + ': ' + activeRecord.value")
public class EipCallParam extends TransientModel {

    private static final long serialVersionUID = -6227914897631164944L;

    public static final String MODEL_MODEL = "eip.EipCallParam";

    @Field.String
    @Field(displayName = "Key")
    private String key;

    @Field.String
    @Field(displayName = "值")
    private String value;

    @Field.Enum
    @Field(displayName = "参数类型")
    private ParamTypeEnum paramType;
}
