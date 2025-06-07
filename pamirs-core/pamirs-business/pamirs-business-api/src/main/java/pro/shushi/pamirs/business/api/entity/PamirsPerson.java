package pro.shushi.pamirs.business.api.entity;

import pro.shushi.pamirs.business.api.model.PamirsPartner;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * FIXME: zbh 20220401 partner多表继承
 */
@Model.model(PamirsPerson.MODEL_MODEL)
@Model.Advanced(unique = {"code"})
@Model(displayName = "个人", summary = "个人", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "P", size = 8)
public class PamirsPerson extends PamirsPartner {

    private static final long serialVersionUID = 3517815085633307860L;

    public static final String MODEL_MODEL = "business.PamirsPerson";
}
