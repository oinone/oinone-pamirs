package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

@Dict(dictionary = BizChannelEnum.dictionary, displayName = "业务渠道")
public class BizChannelEnum extends BaseEnum<BizChannelEnum, String> {

    public static final String dictionary = "pamirs.major.BizChannelEnum";

    public final static BizChannelEnum DEFAULT = create("DEFAULT", "DEFAULT", "默认渠道", "默认渠道");
}