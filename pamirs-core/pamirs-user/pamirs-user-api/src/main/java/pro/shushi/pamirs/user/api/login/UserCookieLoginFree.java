package pro.shushi.pamirs.user.api.login;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.Map;

/**
 *
 * @author shier
 * date 2020/4/11
 *
 * demo -- 完全自定义login的过程
 *   此demo为 cookie方式的完全自定义
 *   需要实现登录部分login 以及拦截部分fetchUserIdByReq
 *   如果fetchUserIdByReq返回值为null的时候 将会被拦截
 *
 */
public class UserCookieLoginFree implements IUserLogin<PamirsUser> {

    @Override
    public PamirsUser login(PamirsUser t) {
        t = cookieLogin(t);
        PamirsSession.setUserId(t.getId());
        return t;
    }

    @Override
    public String type() {
        return UserLoginTypeEnum.COOKIE.value();
    }

    private PamirsUser cookieLogin(PamirsUser t) {
        String sessionId = createSessionId(t);
        PamirsSession.setSessionId(sessionId);
        String cacheKey = parseSessionId(sessionId);
        //缓存SESSION_ID 和用户数据到Redis中
        UserCache.putCache(cacheKey, coverToUserDTO(t));
        //缓存用户新到内存中.UserHook使用到
        UserInfoCache.putUserInfo(t);
        return t;
    }

    public String createSessionId(PamirsUser t){
        return UUIDUtil.getUUIDNumberString();
    }

    public String parseSessionId(String sessionId) {
        return UserConstant.USER_CACHE_KEY + sessionId;
    }

    public PamirsUserDTO coverToUserDTO(PamirsUser t) {
        Map<String, Object> map = JsonUtils.parseObject2Map(t);
        String login = (String) map.get("login");
        String userName = (String) map.get("name");
        String userId = (String) map.get("id");
        //String passWord = (String) map.get("passWord");
        String phone = (String) map.get("phone");
        String email = (String) map.get("email");
        String userCode = (String) map.get("code");
        if (StringUtils.isBlank(login)) {
            throw new RuntimeException();
        }
        PamirsUserDTO pamirsUserDTO = new PamirsUserDTO().setUserName(userName).setLogin(login).setPhone(phone)
                .setUserCode(userCode).setEmail(email).setUserId(Long.valueOf(userId));
        return pamirsUserDTO;
    }

    /**
     * 根据session获取用户
     *
     * @return
     */
    @Override
    public PamirsUserDTO fetchUserIdByReq() {
        String sessionId = PamirsSession.getSessionApi().getSessionId();
        PamirsUserDTO pamirsUserDTO = UserCache.get(sessionId);
        return pamirsUserDTO;
    }

}
