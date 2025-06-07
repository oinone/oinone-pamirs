package pro.shushi.pamirs.eip.designer.model.conn;

import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.designer.model.abs.EipReqParam;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

/**
 * EipReqQueryParam
 *
 * @author yakir on 2023/03/30 14:34.
 */
@Model(displayName = "请求参数")
@Model.model(EipReqQueryParam.MODEL_MODEL)
public class EipReqQueryParam extends EipReqParam {

    private static final long serialVersionUID = 468437010051404045L;

    public final static String MODEL_MODEL = "designer.EipReqQueryParam";

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;

    @Field(displayName = "字段长度")
    @Field.Integer
    private Integer size;

    @Field(displayName = "是否数组")
    @Field.Boolean
    private Boolean isMulti;

    @Field(displayName = "子节点列表")
    @Field.one2many
    @Field.Relation(relationFields = "key", referenceFields = "parentKey")
    private List<EipReqQueryParam> children;
}
