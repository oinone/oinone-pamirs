package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

@Model.model(ResourceTaxKind.MODEL_MODEL)
@Model(labelFields = "name", displayName = "税种")
@Model.Code(sequence = "SEQ", prefix = "TK")
public class ResourceTaxKind extends CodeModel {

    public static final String MODEL_MODEL = "resource.ResourceTaxKind";

    @Field.String
    @Field(displayName = "名称", required = true, unique = true)
    private String name;

}
