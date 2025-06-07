package pro.shushi.pamirs.eip.api.pmodel;

import pro.shushi.pamirs.eip.api.model.EipOpenIpBlacklist;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author yeshenyue on 2025/5/9 17:50.
 */
@Model(displayName = "开放应用IP黑名单代理")
@Model.model(EipOpenIpBlacklistProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class EipOpenIpBlacklistProxy extends EipOpenIpBlacklist {

    public static final String MODEL_MODEL = "pamirs.eip.EipOpenIpBlacklistProxy";
    private static final long serialVersionUID = -6872229351508093214L;
}
