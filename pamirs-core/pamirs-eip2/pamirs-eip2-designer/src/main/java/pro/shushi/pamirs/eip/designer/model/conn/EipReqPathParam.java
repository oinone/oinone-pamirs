package pro.shushi.pamirs.eip.designer.model.conn;

import pro.shushi.pamirs.eip.designer.model.abs.EipReqParam;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 集成接口路径参数
 *
 * @author Adamancy Zhang at 13:46 on 2024-03-20
 */
@Model(displayName = "集成接口路径参数")
@Model.model(EipReqPathParam.MODEL_MODEL)
public class EipReqPathParam extends EipReqParam {

    private static final long serialVersionUID = 7760445758541453389L;

    public static final String MODEL_MODEL = "designer.EipReqPathParam";

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;
}
