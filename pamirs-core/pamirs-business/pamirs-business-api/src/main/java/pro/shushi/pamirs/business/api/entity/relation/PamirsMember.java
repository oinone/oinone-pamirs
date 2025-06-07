package pro.shushi.pamirs.business.api.entity.relation;

import pro.shushi.pamirs.business.api.model.PamirsPartnerRelation;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.meta.annotation.Model;

@Model.MultiTableInherited(type = PamirsMember.RELATION_TYPE)
@Model.model(PamirsMember.MODEL_MODEL)
@Model.Advanced(name = "PamirsMember", unique = {"code"}, index = {"originPartnerCode", "email"})
@Model(displayName = "会员", summary = "会员", labelFields = "name")
public class PamirsMember extends PamirsPartnerRelation implements IDataStatus {

    private static final long serialVersionUID = 4793668161192491850L;

    public static final String MODEL_MODEL = "business.PamirsMember";

    public static final String RELATION_TYPE = "MEMBER";

}
