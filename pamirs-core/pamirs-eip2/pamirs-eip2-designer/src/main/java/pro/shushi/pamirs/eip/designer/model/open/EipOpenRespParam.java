package pro.shushi.pamirs.eip.designer.model.open;

import pro.shushi.pamirs.eip.designer.model.abs.EipRespParam;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

/**
 * EipApiResponseParam
 *
 * @author drome on 2023/03/30 14:34.
 */
@Model(displayName = "响应体")
@Model.model(EipOpenRespParam.MODEL_MODEL)
public class EipOpenRespParam extends EipRespParam {

    private static final long serialVersionUID = -7187685729752424326L;

    public final static String MODEL_MODEL = "designer.EipOpenRespParam";

    @Field(displayName = "子节点列表")
    @Field.many2one
    @Field.Relation(relationFields = "id", referenceFields = "parentId")
    private List<EipOpenRespParam> children;

    /**
     * mock数据,实际不会生成转换. 开放接口业务数据外层, 有固定的数据结构
     */
    @Field.Boolean
    @Field(displayName = "mock数据,整行不允许编辑")
    private Boolean disabledRow;

    @Field.Boolean
    @Field(displayName = "系统数据,根据function自动生成,部分字段禁用编辑")
    private Boolean isSystemData;
}
