package pro.shushi.pamirs.user.api.login;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

/**
 * 用户编码占位符转化
 */
@Slf4j
@Component
public class UserCodePlaceHolderParser extends AbstractPlaceHolderParser {

    @Override
    protected String value() {
        if (PamirsSession.getUserCode() == null) {
            throw PamirsException.construct(BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }
        return PamirsSession.getUserCode();
    }

    @Override
    public String namespace() {
        return "${currentUserCode}";
    }

    @Override
    public String displayName() {
        return "当前用户编码";
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
