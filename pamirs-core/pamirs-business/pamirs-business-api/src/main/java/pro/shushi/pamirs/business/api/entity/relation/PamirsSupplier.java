package pro.shushi.pamirs.business.api.entity.relation;

import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.MultiTableInherited(type = PamirsSupplier.RELATION_TYPE)
@Model.model(PamirsSupplier.MODEL_MODEL)
@Model.Advanced(name = "PamirsSupplier", unique = {"code"})
@Model(displayName = "供应商", summary = "供应商", labelFields = "name")
public class PamirsSupplier extends PamirsPartnerRelation implements IDataStatus {

    private static final long serialVersionUID = -7823326701525807929L;

    public static final String MODEL_MODEL = "business.PamirsSupplier";

    public static final String RELATION_TYPE = "SUPPLIER";

}
