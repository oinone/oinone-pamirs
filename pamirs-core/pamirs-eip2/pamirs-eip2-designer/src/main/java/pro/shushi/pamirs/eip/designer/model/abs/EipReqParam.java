package pro.shushi.pamirs.eip.designer.model.abs;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * EipReqParam
 *
 * @author yakir on 2023/03/30 14:41.
 */
@Model(displayName = "Api 请求抽象")
@Model.model(EipReqParam.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
abstract
public class EipReqParam extends TransientModel {

    private static final long serialVersionUID = -1293883589408946958L;

    public final static String MODEL_MODEL = "designer.EipReqParam";

    @Field(displayName = "参数名")
    @Field.String
    private String key;

    @Field(displayName = "是否必填")
    @Field.Boolean
    private Boolean required;

    @Field(displayName = "默认值")
    @Field.String
    private String defaultValue;

    @Field(displayName = "参数备注", translate = true)
    @Field.String
    private String desc;

    @Field.Integer
    @Field(displayName = "字段层级")
    private Integer depth;

    @Field(displayName = "取值表达式")
    @Field.String(size = 1024)
    private String valueExpr;
}
