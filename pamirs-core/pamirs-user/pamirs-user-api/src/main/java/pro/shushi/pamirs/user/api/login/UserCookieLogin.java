package pro.shushi.pamirs.user.api.login;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.OrmApi;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;

import java.util.Map;
import java.util.Optional;

/**
 * @author shier
 * date 2020/4/10
 */
@Slf4j
public abstract class UserCookieLogin<T extends IdModel> implements IUserLogin<T> {

    public T login(T t) {
        return login(t, null);
    }

    public T login(T t, Integer expiredTime) {
        t = _cookieLogin(t, expiredTime);
        PamirsSession.setUserId(t.getId());
        return t;
    }

    @Override
    public PamirsUserDTO fetchUserIdByReq() {
        String sessionId = PamirsSession.getSessionId();
        return UserCache.get(sessionId);
    }

    private T _cookieLogin(T t, Integer expiredTime) {
        String sessionId = createSessionId();
        PamirsSession.setSessionId(sessionId);
        String cacheKey = parseSessionId(sessionId);
        UserCache.putCache(cacheKey, coverToUserDTO(t), expiredTime);
        return t;
    }

    public void logout() {
        //清理下登录的cookie
        UserCache.logout();
    }

    @Override
    public String type() {
        return UserLoginTypeEnum.COOKIE.value();
    }

    public String createSessionId() {
        return UUIDUtil.getUUIDNumberString();
    }

    public abstract T resolveAndVerification(PamirsUserTransient user);

    public String parseSessionId(String sessionId) {
        return UserConstant.USER_CACHE_KEY + sessionId;
    }

    public PamirsUserDTO coverToUserDTO(T t) {
        Map<String, Object> map = CommonApiFactory.getApi(OrmApi.class).mapping("user.PamirsUser", t);
        String login = (String) map.get("login");
        String userName = (String) map.get("name");
        String userId = map.get("id") + "";
        String phone = (String) map.get("phone");
        String email = (String) map.get("email");
        String userCode = (String) map.get("code");
        if (StringUtils.isBlank(login)) {
            throw new RuntimeException();
        }
        String langCode = Optional.ofNullable(map.get("langId"))
                .map(String::valueOf)
                .filter(StringUtils::isNotBlank)
                .map(Long::parseLong)
                .map(_langId -> new ResourceLang().<ResourceLang>queryById(_langId))
                .map(_lang -> _lang.getCode())
                .orElse(TranslateServiceHolder.get().getDefaultLang());
        PamirsUserDTO pamirsUserDTO = new PamirsUserDTO().setLogin(login).setUserName(userName).setPhone(phone)
                .setUserCode(userCode).setEmail(email).setUserId(Long.valueOf(userId))
                .setLangCode(langCode);
        return pamirsUserDTO;
    }
}
