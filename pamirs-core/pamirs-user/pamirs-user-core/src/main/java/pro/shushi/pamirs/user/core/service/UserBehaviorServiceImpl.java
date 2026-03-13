package pro.shushi.pamirs.user.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.cache.PermissionCache;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.login.UserCookieLogin;
import pro.shushi.pamirs.user.api.login.UserLoginFactory;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsTenantTransient;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserBehaviorService;
import pro.shushi.pamirs.user.api.service.UserLoginSequence;
import pro.shushi.pamirs.user.api.service.UserThirdPartyService;
import pro.shushi.pamirs.user.core.base.manager.UserMobileBehaviorManager;
import pro.shushi.pamirs.user.core.base.manager.WeChatManager;
import pro.shushi.pamirs.user.core.base.service.UserBehaviorBaseService;
import pro.shushi.pamirs.user.core.base.util.UserLoginHelper;

import java.util.Arrays;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;
import static pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum.LOGIN_BY_WECHAT_MA;
import static pro.shushi.pamirs.user.api.enmu.UserExpEnumerate.USER_WX_MINI_APP_SUPPORT;
import static pro.shushi.pamirs.user.api.enmu.UserThirdPartyTypeEnum.WEIXIN_MINI_PROGRAM;
import static pro.shushi.pamirs.user.core.base.util.UserLoginHelper.success;

/**
 * 用户行为服务
 *
 * @author shier
 * date 2020/4/11
 */
@Slf4j
@Component
@Model.model(PamirsUserTransient.MODEL_MODEL)
public class UserBehaviorServiceImpl extends UserBehaviorBaseService implements UserBehaviorService {

    @Autowired
    private UserLoginSequence userLoginSequence;

    @Autowired
    private UserMobileBehaviorManager userMobileBehaviorManager;

    @Autowired
    private UserThirdPartyService userThirdPartyService;

    @Override
    public PamirsUserTransient signUp(PamirsUserTransient user) {
        super.beforeSignUp(user);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        createUserExtpoint(user);
        return success(user);
    }

    @Override
    public PamirsUserTransient signUpMobile(PamirsUserTransient user) {
        String login = userLoginSequence.loginSequence();
        user.setLogin(login);
        user.setName(login);
        super.beforeSignUpMobile(user);
        if (user.getBroken()) {
            log.error("Broken: [{}]", user.getErrorMsg());
            return user;
        }
        PamirsUser rUser = createUserExtpoint(user);
        // 绑定微信小程序第三方登录
        if (StringUtils.isNotBlank(user.getOpenid()) && StringUtils.isNotBlank(user.getUnionId())) {
            userThirdPartyService.addThirdParty(rUser.getId(), user.getOpenid(), user.getUnionId(), WEIXIN_MINI_PROGRAM);
        }

        return success(user);
    }

    @Override
    public PamirsUserTransient loginByMA(PamirsUserTransient user) {
        return userMobileBehaviorManager.loginByMA(user);
    }

    @Override
    public PamirsUserTransient loginMAPhone(PamirsUserTransient user) {
        UserBehaviorEventEnum behaviorEvent = user.getUserBehaviorEvent();
        log.info("Login method: [{}]", behaviorEvent);
        if (LOGIN_BY_WECHAT_MA.equals(behaviorEvent)) {
            String phone = BeanDefinitionUtils.getBean(WeChatManager.class).wechatPhoneNo(user.getToken());
            user.setPhone(phone);
            user.unsetToken();
            return success(user);
        } else {
            throw PamirsException.construct(USER_WX_MINI_APP_SUPPORT).errThrow();
        }
    }

    /**
     * 使用token登录
     *
     * @param user
     * @return
     */
    @Override
    public PamirsUserTransient loginByCookie(PamirsUser user) {
        return loginByCookie(user, true);
    }

    /**
     * 登录后不跳转
     *
     * @param user
     * @return
     */
    @Override
    public PamirsUserTransient loginWithoutRedirect(PamirsUser user) {
        return loginByCookie(user, false);
    }

    @Override
    public void clearSessionByUid(Long userId) {
        UserCache.clearSessionByUid(userId);
    }

    @Function(openLevel = {LOCAL, REMOTE, API})
    public PamirsUserDTO currentUser() {
        return super.currentUser();
    }

    private PamirsUserTransient loginByCookie(PamirsUser user, Boolean redirect) {
        //session处理
        return loginUserByCookie(user, redirect);
    }


    private PamirsUserTransient loginUserByCookie(PamirsUser user, Boolean redirect) {
        PamirsUserTransient result = new PamirsUserTransient();
        CommonApiFactory.getApi(PermissionCache.class).clear();
        UserCookieLogin userCookieLogin = (UserCookieLogin) UserLoginFactory.getUserLogin(UserLoginTypeEnum.COOKIE.value());

        //登录
        userCookieLogin.login(user);
        String tenant = PamirsTenantSession.getTenant();
        if (StringUtils.isNotBlank(tenant)) {
            PamirsTenantTransient tenantInfo = new PamirsTenantTransient();
            tenantInfo.setTenant(tenant).setDisplayName(tenant);
            result.setTenants(Arrays.asList(tenantInfo));
            result.setTenant(tenant);
        }
        if (redirect != null && redirect) {
            result.setRedirect(UserLoginHelper.getRedirectMenu());
        }
        return success(result);
    }

}
