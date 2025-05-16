package pro.shushi.pamirs.sso.oauth2.client.login;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.user.api.login.UserTokenLogin;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

import java.util.Map;

/**
 * @author shier
 * date 2020/4/10
 */
@Component
public class SsoUserTokenLoginSimpleClient extends SsoUserTokenLoginClient<PamirsUser> {

    @Override
    public String genToken(PamirsUser pamirsUser) {
        return JwtTokenUtil.generateToken(pamirsUser.getId().toString());
    }

    @Override
    public Long fetchUserIdByToken(String token) {
        String keyFromToken = JwtTokenUtil.getKeyFromToken(token);
        Map map = JsonUtils.parseObject(keyFromToken, Map.class);
        return Long.parseLong(map.get("openId").toString());
    }

    @Override
    public PamirsUser getTokenUser(Long userId) {
        PamirsUser user = (PamirsUser) new PamirsUser().setId(userId);
        return user.queryOne();
    }


}
