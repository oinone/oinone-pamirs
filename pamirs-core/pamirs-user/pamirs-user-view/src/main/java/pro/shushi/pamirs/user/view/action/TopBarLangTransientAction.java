package pro.shushi.pamirs.user.view.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.enmu.UserExpEnumerate;
import pro.shushi.pamirs.user.api.login.UserInfoCache;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.TopBarLangTransientModel;

import java.util.Objects;
import java.util.Optional;

/**
 * @author shier
 * date 2019-07-18
 */
@Base
@Component
@Fun(TopBarLangTransientModel.MODEL_MODEL)
public class TopBarLangTransientAction {

    /**
     * 右上角激活语言包
     *
     * @param topBarLangTransientModel 当前用户从前端传递
     * @return
     */
    @Action(displayName = "切换语言")
    @Action.Advanced(invisible = "true")
    public TopBarLangTransientModel activeLang(TopBarLangTransientModel topBarLangTransientModel) {
        try {
            //查找选中的语言包
            Long langId = topBarLangTransientModel.getId();
            if (Objects.isNull(langId)) {
                throw PamirsException.construct(UserExpEnumerate.USER_TOPBAR_NO_LANG).errThrow();
            }

            // TODO:语言缓存
            ResourceLang lang = new ResourceLang().setId(langId).queryById();
            Long userId = PamirsSession.getUserId();
            PamirsUser user = new PamirsUser().setId(userId).queryById();
            if (Objects.isNull(user)) {
                throw PamirsException.construct(UserExpEnumerate.USER_TOPBAR_NO_USER).errThrow();
            }
            user.setLang(lang);
            PamirsUser updateUser = new PamirsUser();
            updateUser.setId(userId);
            updateUser.setLang(lang);

            //全局绑定语言包
            updateUser.updateById();
            Optional.of(user).map(PamirsUser::getCurrency).map(ResourceCurrency::getId)
                    .map(_id -> new ResourceCurrency().<ResourceCurrency>queryById(_id))
                    .ifPresent(user::setCurrency);
            updateCacheUserInfo(user);
            PamirsSession.setUserId(userId);
            PamirsSession.setLang(lang.getCode());
            topBarLangTransientModel.setId(langId);
        } catch (Exception e) {
            throw PamirsException.construct(UserExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        return topBarLangTransientModel;

    }

    private void updateCacheUserInfo(PamirsUser pamirsUser) {
        String sessionId = PamirsSession.getSessionApi().getSessionId();
        PamirsUserDTO user = UserCache.get(sessionId);
        if (user != null) {
            String langCode = Optional.ofNullable(pamirsUser.getLang()).map(ResourceLang::getCode).orElse(DefaultResourceConstants.CHINESE_LANGUAGE_CODE);
            PamirsSession.setLang(langCode);
            user.setLangCode(langCode);
            String cacheKey = UserCache.parseSessionId(sessionId);
            UserCache.putCache(cacheKey, user);
            UserInfoCache.clearUserById(user.getUserId());
        }
    }
}
