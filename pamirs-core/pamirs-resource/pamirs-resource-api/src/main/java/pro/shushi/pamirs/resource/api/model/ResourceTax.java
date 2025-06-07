package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.math.BigDecimal;
import java.util.List;

@Model.model(ResourceTax.MODEL_MODEL)
@Model(labelFields = {"name"}, displayName = "税率")
@Model.Code(sequence = "SEQ", prefix = "T")
public class ResourceTax extends CodeModel {

    public static final String MODEL_MODEL = "resource.ResourceTax";

    @Field.String
    @Field(displayName = "名称", required = true, unique = true)
    private String name;

    @Field.Float
    @Field(displayName = "税率", required = true)
    private BigDecimal tax;

    @Field.many2one
    @Field(displayName = "国家地区")
    private ResourceRegion region;

    @Field.many2one
    @Field(displayName = "税种", required = true)
    private ResourceTaxKind taxKind;

    @Field.one2many
    @Field(displayName = "外部资源关联列表")
    @Field.Relation(relationFields = {"code", CharacterConstants.SEPARATOR_OCTOTHORPE + ResourceTax.MODEL_MODEL + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"relationCode", "model"})
    private List<OutResourceRelation> outResourceRelationList;

}
