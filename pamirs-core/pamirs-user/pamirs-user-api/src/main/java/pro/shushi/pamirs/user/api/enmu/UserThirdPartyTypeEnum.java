package pro.shushi.pamirs.user.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

@Dict(dictionary = "userThirdPartyTypeEnum", displayName = "第三方账号类型")
public class UserThirdPartyTypeEnum extends BaseEnum<UserThirdPartyTypeEnum, String> {

    public static final UserThirdPartyTypeEnum WORK_WEIXIN = create("WORK_WEIXIN","WORK_WEIXIN","企业微信");
    public static final UserThirdPartyTypeEnum WEIXIN = create("WEIXIN","WEIXIN","微信");
    public static final UserThirdPartyTypeEnum WEIXIN_MINI_PROGRAM = create("WEIXIN_MINI_PROGRAM","WEIXIN_MINI_PROGRAM","微信小程序");
    public static final UserThirdPartyTypeEnum QQ = create("QQ","QQ","QQ");
    public static final UserThirdPartyTypeEnum WEIBO = create("WEIBO","WEIBO","微博");
    public static final UserThirdPartyTypeEnum DINGTALK = create("DINGTALK","DINGTALK","钉钉");
}
