package pro.shushi.pamirs.business.api.entity.relation;

import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.MultiTableInherited(type = PamirsDistributor.RELATION_TYPE)
@Model.model(PamirsDistributor.MODEL_MODEL)
@Model.Advanced(name = "PamirsDistributor", unique = {"code"})
@Model(displayName = "经销商", summary = "经销商", labelFields = "name")
public class PamirsDistributor extends PamirsPartnerRelation {

    private static final long serialVersionUID = -4836257745507654275L;

    public static final String MODEL_MODEL = "business.PamirsDistributor";

    public static final String RELATION_TYPE = "DISTRIBUTOR";


}
