package pro.shushi.pamirs.eip.designer.model.conn;

import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.designer.model.abs.EipReqParam;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * EipReqHeaderParam
 *
 * @author yakir on 2023/03/30 14:34.
 */
@Model(displayName = "请求头参数")
@Model.model(EipReqHeaderParam.MODEL_MODEL)
public class EipReqHeaderParam extends EipReqParam {

    private static final long serialVersionUID = 7760445758541453389L;

    public final static String MODEL_MODEL = "designer.EipReqHeaderParam";

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;
}
