package pro.shushi.pamirs.user.api.login;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

/**
 * 用户占位符转化
 *
 * @author shier
 * date  2020/5/7 3:34 下午
 */
@Slf4j
@Component
public class UserPlaceHolderParser extends AbstractPlaceHolderParser {

    @Override
    protected String value() {
        if (PamirsSession.getUserId() == null) {
            throw PamirsException.construct(BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }
        return PamirsSession.getUserId().toString();
    }

    @Override
    public String namespace() {
        return "${currentUser}";
    }

    @Override
    public Integer priority() {
        return 0;
    }

    @Override
    public Boolean active() {
        return Boolean.TRUE;
    }
}
