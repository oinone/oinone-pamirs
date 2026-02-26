package pro.shushi.pamirs.auth.api.runtime.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.runtime.session.AuthFunctionPermissionSession;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;

/**
 * 简单权限验证
 *
 * @author Adamancy Zhang at 19:11 on 2026-02-25
 */
@Base
@Slf4j
@Component
public class SimpleFunctionPermissionHook implements HookBefore {

    @Hook(priority = 10)
    @Override
    public Object run(Function function, Object... args) {
        if (AuthFunctionPermissionSession.isVerified()) {
            return true;
        }
        if (isAccessFunction(function)) {
            AuthFunctionPermissionSession.passed();
            return function;
        } else if (PamirsSession.getUserId() == null) {
            //如果没有权限 且没有登录 跳转到登录
            throw PamirsException.construct(BaseExpEnumerate.BASE_USER_NOT_LOGIN_ERROR).errThrow();
        }
        if (log.isWarnEnabled()) {
            log.warn("Function access denied. namespace: {}, fun: {}, source: {}", function.getNamespace(), function.getFun(), function.getSource());
        }
        throw PamirsException.construct(AuthExpEnumerate.AUTH_NO_PERMISSION).errThrow();
    }

    private boolean isAccessFunction(Function function) {
        AuthApi authApi = AuthApi.get();
        FunctionSourceEnum functionSource = function.getSource();
        String model = function.getNamespace();
        String fun = function.getFun();
        if (FunctionSourceEnum.FUNCTION.equals(functionSource) || FunctionSourceEnum.ACTION.equals(functionSource)) {
            return authApi.canAccessFunction(model, fun).getSuccess();
        }
        return true;
    }
}
