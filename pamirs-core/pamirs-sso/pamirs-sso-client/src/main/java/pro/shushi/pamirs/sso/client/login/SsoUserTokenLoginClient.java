package pro.shushi.pamirs.sso.client.login;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.login.IUserLogin;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author shier
 * date 2020/4/10
 */
public abstract class SsoUserTokenLoginClient<T extends IdModel> implements IUserLogin<T> {

    public String login(T t, HttpServletRequest request, HttpServletResponse response) {
        String token = genToken(t);
        //支持不同端的请求，通过source字段区分，支持空
        String source = request.getHeader(UserConstant.SOURCE);
        UserCache.putToken(token, t.getId(), source);
        PamirsSession.setUserId(t.getId());
        return token;
    }

    public String login(T t) {
        //session处理
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        return login(t, request, response);
    }

    public abstract String genToken(T t);

    @Override
    public PamirsUserDTO fetchUserIdByReq() {
        return fetchUserByToken();
    }

    @Override
    public String type() {
        return UserLoginTypeEnum.OAUTH.value();
    }

    /**
     * 根据token解析用户
     *
     * @return
     */
    public PamirsUserDTO fetchUserByToken() {
        String authHeader = PamirsSession.getRequestVariables().getHeader(UserConstant.USER_TOKEN_HEADER);
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String authHeader = request.getHeader(UserConstant.USER_TOKEN_HEADER);
        String tokenHead = UserConstant.USER_TOKEN_PREFIX;
        //支持不同端的请求，通过source字段区分，支持空
//        String source = request.getHeader(UserConstant.SOURCE);
        String source = PamirsSession.getRequestVariables().getHeader(UserConstant.SOURCE);

        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(tokenHead.length());
            Long userId = fetchUserIdByToken(authToken);

            if (null != userId) {
                //查询当前的redis中存储的token 单点登录
                PamirsUser user = getTokenUser(userId);
                if (user == null) {
                    return null;
                }
                String token = UserCache.getToken(userId, source);
                if (StringUtils.isBlank(token)) {
                    UserCache.putToken(authToken, userId, source);
                }
                // FIXME: zbh 20210915 @shier 验证token成功后，需延长token有效时间，否则用户长时间操作会异常登出
                // token目前的有效时间是5天

                String langCode = Optional.ofNullable(user.getLangId())
                        .filter(ObjectUtils::isNotEmpty)
                        .map(_langId -> new ResourceLang().<ResourceLang>queryById(_langId))
                        .map(_lang -> _lang.getCode())
                        .orElse(DefaultResourceConstants.CHINESE_LANGUAGE_CODE);

                return new PamirsUserDTO().setLogin(user.getLogin()).setUserName(user.getName()).setEmail(user.getEmail())
                        .setUserCode(user.getCode()).setPhone(user.getPhone()).setUserId(userId)
                        .setLangCode(langCode);
            }
        }
        return null;
    }

    public abstract Long fetchUserIdByToken(String token);


    public PamirsUser getTokenUser(Long userId) {
        return null;
    }

}
