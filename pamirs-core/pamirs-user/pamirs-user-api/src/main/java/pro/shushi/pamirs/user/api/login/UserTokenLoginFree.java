package pro.shushi.pamirs.user.api.login;

import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.user.api.cache.UserCache;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.enmu.UserLoginTypeEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

/**
 * @author shier
 * date 2020/4/11
 * <p>
 * demo -- 完全自定义login的过程
 * 此demo为 token方式的完全自定义
 * 需要实现登录部分login 以及拦截部分fetchUserIdByReq
 * 如果fetchUserIdByReq返回值为null的时候 将会被拦截
 */
public class UserTokenLoginFree implements IUserLogin<PamirsUser> {

    @Override
    public String login(PamirsUser user) {
        String token = genToken(user);
        //支持不同端的请求，通过source字段区分，支持空
        String source = PamirsSession.getRequestVariables().getHeader(UserConstant.SOURCE);
        UserCache.putToken(token, user.getId(), source);
        PamirsSession.setUserId(user.getId());
        return token;
    }

    /**
     * 根据session获取用户
     *
     * @return
     */
    @Override
    public PamirsUserDTO fetchUserIdByReq() {
        return fetchUserByToken();
    }

    @Override
    public String type() {
        return UserLoginTypeEnum.TOKEN.value();
    }
    /*-------------------------------------以下为demo的业务部分展示-----------------------------------*/


    /**
     * 根据token解析用户
     *
     * @return
     */
    private PamirsUserDTO fetchUserByToken() {
        String authHeader = PamirsSession.getRequestVariables().getHeader(UserConstant.USER_TOKEN_HEADER);
        String tokenHead = UserConstant.USER_TOKEN_PREFIX;
        //支持不同端的请求，通过source字段区分，支持空
        String source = PamirsSession.getRequestVariables().getHeader(UserConstant.SOURCE);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(tokenHead.length());
            Long userId = fetchUserIdByToken(authToken);
            if (null != userId) {
                //查询当前的redis中存储的token 单点登录
                String token = UserCache.getToken(userId, source);
                if (token == null || !authHeader.equals(token)) {
                    return null;
                }
                if (tokenCheck(authToken, userId)) {
                    PamirsUser user = (PamirsUser) new PamirsUser().setId(userId);
                    user = user.queryOne();
                    return new PamirsUserDTO().setUserName(user.getName()).setLogin(user.getLogin()).setEmail(user.getEmail())
                            .setUserCode(user.getCode()).setPhone(user.getPhone()).setUserId(userId);
                }
            }
        }
        return null;
    }

    public Long fetchUserIdByToken(String token) {
        Long id = Long.valueOf(JwtTokenUtil.getKeyFromToken(token));
        return id;
    }

    public Boolean tokenCheck(String token, Long userId) {
        PamirsUser user = (PamirsUser) new PamirsUser().setId(userId);
        user = user.queryOne();
        if (null == user) {
            return Boolean.FALSE;
        }
        return JwtTokenUtil.validateToken(token, userId.toString());
    }

    public String genToken(PamirsUser pamirsUser) {
        return JwtTokenUtil.generateToken(pamirsUser.getId().toString());
    }
}
