package pro.shushi.pamirs.eip.designer.model.open;

import pro.shushi.pamirs.eip.designer.model.abs.EipReqParam;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

/**
 * EipApiBody
 *
 * @author yakir on 2023/03/30 14:34.
 */
@Model(displayName = "请求体参数")
@Model.model(EipOpenReqBodyParam.MODEL_MODEL)
public class EipOpenReqBodyParam extends EipReqParam {

    private static final long serialVersionUID = -3428541914495606750L;

    public final static String MODEL_MODEL = "designer.EipOpenReqBodyParam";

    @Field(displayName = "参数类型")
    @Field.Enum
    private ParamTypeEnum paramType;

    @Field(displayName = "字段长度")
    @Field.Integer
    private Integer size;

    @Field(displayName = "是否数组")
    @Field.Boolean
    private Boolean isMulti;

    @Field(displayName = "取值表达式")
    @Field.String(size = 1024)
    private String valueExpr;

    @Field(displayName = "子节点列表")
    @Field.many2one
    @Field.Relation(relationFields = "id", referenceFields = "parentId")
    private List<EipOpenReqBodyParam> children;

    @Field.Boolean
    @Field(displayName = "系统数据,根据function自动生成,部分字段禁用编辑")
    private Boolean isSystemData;
}
