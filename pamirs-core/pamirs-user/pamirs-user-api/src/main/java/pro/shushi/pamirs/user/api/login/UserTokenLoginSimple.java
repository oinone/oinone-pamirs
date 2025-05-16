package pro.shushi.pamirs.user.api.login;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.utils.JwtTokenUtil;

/**
 * @author shier
 * date 2020/4/10
 */
@Component
public class UserTokenLoginSimple extends UserTokenLogin<PamirsUser> {

    @Override
    public String genToken(PamirsUser pamirsUser) {
        return JwtTokenUtil.generateToken(pamirsUser.getId().toString());
    }

    @Override
    public Long fetchUserIdByToken(String token) {
        Long id = Long.valueOf(JwtTokenUtil.getKeyFromToken(token));
        return id;
    }

    @Override
    public Boolean tokenCheck(String token, Long userId) {
        PamirsUser user = (PamirsUser) new PamirsUser().setId(userId);
        user = user.queryOne();
        if (null==user){
            return Boolean.FALSE;
        }
        return JwtTokenUtil.validateToken(token, userId.toString());
    }
}
