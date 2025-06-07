package pro.shushi.pamirs.eip.designer.model.abs;

import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * EipRespParam
 *
 * @author yakir on 2023/03/30 14:41.
 */
@Model(displayName = "Api 响应抽象")
@Model.model(EipRespParam.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
abstract
public class EipRespParam extends TransientModel {

    private static final long serialVersionUID = -1293883589408946958L;

    public final static String MODEL_MODEL = "designer.EipRespParam";

    @Field(displayName = "参数名")
    @Field.String
    private String key;

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;

    @Field(displayName = "是否数组")
    @Field.Boolean
    private Boolean isMulti;

    @Field(displayName = "取值表达式")
    @Field.String(size = 1024)
    private String valueExpr;

    @Field(displayName = "备注", translate = true)
    @Field.String
    private String desc;

    @Field.Integer
    @Field(displayName = "字段层级")
    private Integer depth;
}
