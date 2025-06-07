package pro.shushi.pamirs.eip.designer.model.conn;

import pro.shushi.pamirs.eip.designer.enumeration.ApiParamOrigin;
import pro.shushi.pamirs.eip.designer.model.abs.EipRespParam;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

/**
 * EipApiResponseParam
 *
 * @author yakir on 2023/03/30 14:34.
 */
@Model(displayName = "响应体")
@Model.model(EipApiResponseParam.MODEL_MODEL)
@Model.Advanced(index = {"responseId"})
public class EipApiResponseParam extends EipRespParam {

    private static final long serialVersionUID = -7455875618723582213L;

    public final static String MODEL_MODEL = "designer.EipApiResponseParam";

    @Field(displayName = "参数来源")
    @Field.Enum
    private ApiParamOrigin paramOrigin;

    @Field(displayName = "ParentKey")
    @Field.String
    private String parentKey;

    @Field(displayName = "子节点列表")
    @Field.one2many
    @Field.Relation(relationFields = "key", referenceFields = "parentKey")
    private List<EipApiResponseParam> children;
}
