package pro.shushi.pamirs.user.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.user.api.enmu.UserThirdPartyTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUserThirdParty;

/**
 * 第三方账号识别
 * 除user.login+password外的登录都输入绑定第三方识别系统登录，包括
 * 1. 通过验证码识别绑定的： user.email、user.phone
 * 2. 通过第三方账户体系绑定的：微信公众号、微信小程序、支付宝账号、微博账号
 * 3. 通过运营商网络识别的：中国电信、中国移动的一键本机登录
 */
@Fun(UserThirdPartyService.FUN_NAMESPACE)
public interface UserThirdPartyService {

    String FUN_NAMESPACE = "pamirs.user.api.UserThirdPartyService";

    /**
     * 绑定第三方登录
     *
     * @param userId
     * @param openId
     * @param unionId
     * @param userThirdPartyType
     * @return
     */
    @Function
    PamirsUserThirdParty addThirdParty(Long userId, String openId, String unionId, UserThirdPartyTypeEnum userThirdPartyType);

    @Function
    PamirsUserThirdParty queryByUserId(Long userId, UserThirdPartyTypeEnum userThirdPartyType);

    @Function
    PamirsUserThirdParty queryByOpenId(String openId, UserThirdPartyTypeEnum userThirdPartyType);
}
