package pro.shushi.pamirs.business.api.entity.relation;

import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.MultiTableInherited(type = PamirsSeller.RELATION_TYPE)
@Model.model(PamirsSeller.MODEL_MODEL)
@Model.Advanced(name = "PamirsSeller", unique = {"code"})
@Model(displayName = "卖家", summary = "卖家", labelFields = "name")
public class PamirsSeller extends PamirsPartnerRelation {

    private static final long serialVersionUID = 123909865325758175L;

    public static final String MODEL_MODEL = "business.PamirsSeller";

    public static final String RELATION_TYPE = "SELLER";

}
