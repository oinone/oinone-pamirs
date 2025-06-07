package pro.shushi.pamirs.eip.designer.model.open;

import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.designer.model.abs.EipReqParam;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 开放接口路径参数
 *
 * @author Adamancy Zhang at 13:46 on 2024-03-20
 */
@Model(displayName = "开放接口路径参数")
@Model.model(EipOpenReqPathParam.MODEL_MODEL)
public class EipOpenReqPathParam extends EipReqParam {

    private static final long serialVersionUID = 7842497873779955585L;

    public static final String MODEL_MODEL = "designer.EipOpenReqPathParam";

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;

}
