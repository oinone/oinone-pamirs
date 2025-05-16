package pro.shushi.pamirs.user.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.user.api.service.UserThirdPartyService;
import pro.shushi.pamirs.user.api.enmu.UserThirdPartyTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUserThirdParty;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;

import java.util.Date;

@Slf4j
@Component
@Fun(UserThirdPartyServiceImpl.FUN_NAMESPACE)
public class UserThirdPartyServiceImpl implements UserThirdPartyService {

    @Override
    @Function
    public PamirsUserThirdParty addThirdParty(Long userId, String openId, String unionId, UserThirdPartyTypeEnum userThirdPartyType) {
        PamirsUserThirdParty userThirdParty = new PamirsUserThirdParty();
        userThirdParty.setOpenid(openId);
        userThirdParty.setThirdPartyType(userThirdPartyType);

        PamirsUserThirdParty userThirdPartyExist = userThirdParty.queryOne();
        userThirdParty.setUserId(userId);
        userThirdParty.setUnionId(unionId);
        userThirdParty.setLastLoginTime(new Date());
        if (userThirdPartyExist == null) {
            userThirdParty.create();
        } else {
            userThirdParty.setId(userThirdPartyExist.getId());
            userThirdParty.updateById();
        }

        return userThirdParty;
    }

    @Override
    @Function
    public PamirsUserThirdParty queryByUserId(Long userId, UserThirdPartyTypeEnum userThirdPartyType) {
        if (userId == null || userId < 0) {
            throw PamirsException.construct(UserExpEnumerate.THIRD_PARTY_USERNAME_IS_BLANK).errThrow();
        }
        if (userThirdPartyType == null) {
            throw PamirsException.construct(UserExpEnumerate.THIRD_PARTY_TYPE_IS_NULL).errThrow();
        }
        return new PamirsUserThirdParty().setThirdPartyType(userThirdPartyType).setUserId(userId).queryOne();
    }

    @Override
    @Function
    public PamirsUserThirdParty queryByOpenId(String openId, UserThirdPartyTypeEnum userThirdPartyType) {
        if (StringUtils.isBlank(openId)) {
            throw PamirsException.construct(UserExpEnumerate.THIRD_PARTY_USERNAME_IS_BLANK).errThrow();
        }
        if (userThirdPartyType == null) {
            throw PamirsException.construct(UserExpEnumerate.THIRD_PARTY_TYPE_IS_NULL).errThrow();
        }
        return new PamirsUserThirdParty().setThirdPartyType(userThirdPartyType).setOpenid(openId).queryOne();
    }
}
