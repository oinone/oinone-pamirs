package pro.shushi.pamirs.eip.designer.model.open;

import pro.shushi.pamirs.eip.designer.model.abs.EipReqParam;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * EipReqQueryParam
 *
 * @author yakir on 2023/03/30 14:34.
 */
@Model(displayName = "请求参数")
@Model.model(EipOpenReqQueryParam.MODEL_MODEL)
public class EipOpenReqQueryParam extends EipReqParam {

    private static final long serialVersionUID = 2510706653083990479L;

    public final static String MODEL_MODEL = "designer.EipOpenReqQueryParam";

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;

    @Field(displayName = "取值表达式")
    @Field.String(size = 1024)
    private String valueExpr;

    @Field.Boolean
    @Field(displayName = "系统数据,根据function自动生成,部分字段禁用编辑")
    private Boolean isSystemData;
}
