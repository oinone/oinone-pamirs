package pro.shushi.pamirs.user.api.hook;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.user.api.login.IUserLogin;
import pro.shushi.pamirs.user.api.login.LoginTypeParser;
import pro.shushi.pamirs.user.api.login.UserInfoCache;
import pro.shushi.pamirs.user.api.login.UserLoginFactory;

import static pro.shushi.pamirs.user.api.cache.UserCache.logout;

@Base
@Slf4j
@Component
public class UserHook<T extends IdModel> implements HookBefore {

    @Override
    @Hook(priority = 0)
    public Object run(Function function, Object... args) {
        UserInfoCache.init();
        if (PamirsSession.getUserId() == null &&
                !("user.PamirsUserTransient".equals(function.getNamespace()) && "login".equals(function.getFun()))
        ) {
            try {
                setUser(function);
            } catch (Exception e) {
                log.error("【用户状态异常】{},当前方法为{}工作空间为{}", BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR.msg(), function.getFun(), function.getNamespace(), e);
                throw PamirsException.construct(BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR, e).errThrow();
            }
        }
        putEnv();
        return null;
    }

    private void setUser(Function event) {
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
            }
        }
    }

    /**
     * 将用户信息放入上下文（V2不放置公司信息）
     *
     * @param user
     */
    protected void putEnv(PamirsUserDTO user) {
        PamirsSession.setUserId(user.getUserId());
        PamirsSession.setUserName(user.getUserName());
        PamirsSession.setLang(user.getLangCode());
        PamirsSession.setUserCode(user.getUserCode());
    }

    protected void putEnv() {
        if (PamirsSession.getLang() == null) {
            String lang = FetchUtil.fetchLang();
            PamirsSession.setLang(lang);
        }
    }
}

