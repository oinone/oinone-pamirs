package pro.shushi.pamirs.user.api.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.login.IUserLogin;
import pro.shushi.pamirs.user.api.login.LoginTypeParser;
import pro.shushi.pamirs.user.api.login.UserLoginFactory;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.spi.UserSessionApi;

/**
 * @author Adamancy Zhang at 13:49 on 2025-10-20
 */
@Order
@Service
@SPI.Service
@Slf4j
public class DefaultUserSessionService implements UserSessionApi {

    @Override
    public void login(Function function, Object... args) {
        boolean isLogin = false;
        if (PamirsSession.getUserId() == null &&
                !(PamirsUserTransient.MODEL_MODEL.equals(function.getNamespace()) && "login".equals(function.getFun()))
        ) {
            try {
                isLogin = setUser(function);
            } catch (Exception e) {
                log.error("【用户状态异常】{},当前方法为{}工作空间为{}", BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR.msg(), function.getFun(), function.getNamespace(), e);
                throw PamirsException.construct(BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR, e).errThrow();
            }
        }
        if (!isLogin) {
            putEnv();
        }
    }

    protected boolean setUser(Function event) {
        if (null != RequestContextHolder.getRequestAttributes()) {
            String loginType = LoginTypeParser.getLoginType();
            IUserLogin userLogin = UserLoginFactory.getUserLogin(loginType);
            if (userLogin == null) {
                log.error("user login factory error. loginType: {}, namespace: {}, fun: {}", loginType, event.getNamespace(), event.getFun());
                throw PamirsException.construct(BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR).errThrow();
            }
            PamirsUserDTO userDTO = userLogin.fetchUserIdByReq();
            if (null == userDTO) {
                log.error("fetch user error. loginType: {}, factory: {}, namespace: {}, fun: {}", loginType, userLogin.getClass().getName(), event.getNamespace(), event.getFun());
                //清理下登录的cookie
                logout();
            } else {
                putEnv(userDTO);
                return true;
            }
        }
        return false;
    }

    @Override
    public void putEnv(PamirsUserDTO user) {
        PamirsSession.setUserId(user.getUserId());
        PamirsSession.setUserName(user.getUserName());
        PamirsSession.setLang(user.getLangCode());
        PamirsSession.setUserCode(user.getUserCode());
    }

    @Override
    public void putEnv() {
        if (PamirsSession.getLang() == null) {
            String lang = FetchUtil.fetchLang();
            PamirsSession.setLang(lang);
        }
    }

    @Override
    public void logout() {
        UserCache.logout();
    }
}
