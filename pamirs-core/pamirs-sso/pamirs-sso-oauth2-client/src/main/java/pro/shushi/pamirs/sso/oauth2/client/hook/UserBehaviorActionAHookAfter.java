package pro.shushi.pamirs.sso.oauth2.client.hook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.sso.oauth2.client.utils.Oauth2AuthenticateUtils;
import pro.shushi.pamirs.user.api.model.tmodel.PamirsUserTransient;
import pro.shushi.pamirs.user.api.service.UserBehaviorService;

@Component
public class UserBehaviorActionAHookAfter implements HookAfter {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Override
    @Hook(priority = 10, displayName = "退出登录拦截器")
    public Object run(Function function, Object ret) {
        FunctionDefinition functionDefinition = function.getFunctionDefinition();
        if (PamirsUserTransient.MODEL_MODEL.equals(functionDefinition.getNamespace()) && functionDefinition.getFun().equals("logout")) {
            Oauth2AuthenticateUtils.logout();
        }
        return function;
    }
}
