package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

@Dict(dictionary = ResourceBankStatusEnum.DICTIONARY, displayName = "开户行状态")
public class ResourceBankStatusEnum extends BaseEnum<ResourceBankStatusEnum, String> {
    public static final String DICTIONARY = "pamirs.resource.ResourceBankStatusEnum";

    public final static ResourceBankStatusEnum ENABLE = create("TRUE", "TRUE", "正常", "正常");
    public final static ResourceBankStatusEnum DISABLE = create("DISABLE", "DISABLE", "禁用", "禁用");
}
