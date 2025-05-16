package pro.shushi.pamirs.eip.designer.model.open;

import pro.shushi.pamirs.eip.designer.model.abs.EipReqParam;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 开放接口请求头参数
 *
 * @author Adamancy Zhang at 18:50 on 2024-03-12
 */
@Model(displayName = "开放接口请求头参数")
@Model.model(EipOpenReqHeaderParam.MODEL_MODEL)
public class EipOpenReqHeaderParam extends EipReqParam {

    private static final long serialVersionUID = 7842497873779955585L;

    public static final String MODEL_MODEL = "designer.EipOpenReqHeaderParam";

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;

}
