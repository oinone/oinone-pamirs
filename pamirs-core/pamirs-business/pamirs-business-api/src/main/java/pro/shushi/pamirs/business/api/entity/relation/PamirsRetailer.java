package pro.shushi.pamirs.business.api.entity.relation;

import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.MultiTableInherited(type = PamirsRetailer.RELATION_TYPE)
@Model.model(PamirsRetailer.MODEL_MODEL)
@Model.Advanced(name = "PamirsRetailer", unique = {"code"})
@Model(displayName = "零售商", summary = "零售商", labelFields = "name")
public class PamirsRetailer extends PamirsPartnerRelation implements IDataStatus {

    public static final String MODEL_MODEL = "business.PamirsRetailer";

    public static final String RELATION_TYPE = "RETAILER";

}
