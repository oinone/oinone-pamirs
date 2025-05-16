package pro.shushi.pamirs.auth.api.runtime.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.runtime.session.AuthFunctionPermissionSession;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessResourceInfoPrepareApi;
import pro.shushi.pamirs.boot.base.enmu.BaseExpEnumerate;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.Optional;

/**
 * 函数权限Hook
 *
 * @author Adamancy Zhang at 16:24 on 2024-01-06
 */
@Base
@Slf4j
@Component
public class FunctionPermissionHook implements HookBefore {

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

    /**
     * <p>function using namespace and fun verify permission.</p>
     * <p>action using namespace and name verify permission.</p>
     * <p>
     * PS: action name always equal function name.
     * </p>
     *
     * @param function access function
     * @return is access
     */
    private boolean isAccessFunction(Function function) {
        AuthApi authApi = AuthApi.get();
        FunctionSourceEnum functionSource = function.getSource();
        String model = function.getNamespace();
        String fun = function.getFun();
        String name = function.getName();

        ServerAction serverAction = getServerAction();
        if (serverAction != null) {
            if (fun.equals(serverAction.getFun())) {
                functionSource = FunctionSourceEnum.ACTION;
                name = serverAction.getName();
            } else {
                return false;
            }
        }

        switch (functionSource) {
            case FUNCTION:
                prepareAccessResourceInfo(function, model, fun, FunctionSourceEnum.FUNCTION);
                return authApi.canAccessFunction(model, fun).getSuccess();
            case ACTION:
                prepareAccessResourceInfo(function, model, name, FunctionSourceEnum.ACTION);
                return authApi.canAccessAction(model, name).getSuccess();
        }
        return true;
    }

    private ServerAction getServerAction() {
        return Optional.ofNullable(AccessResourceInfoSession.getInfo())
                .map(AccessResourceInfo::getLastPath)
                .filter(v -> ResourcePathMetadataType.ACTION.equals(v.getType()))
                .map(v -> {
                    Action action = PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(v.getModel(), v.getName());
                    if (action instanceof ServerAction) {
                        return (ServerAction) action;
                    }
                    return null;
                })
                .filter(v -> SystemSourceEnum.UI.equals(v.getSystemSource()))
                .orElse(null);
    }

    private void prepareAccessResourceInfo(Function function, String namespace, String fun, FunctionSourceEnum source) {
        AccessResourceInfo info = AccessResourceInfoSession.getInfo();
        if (info == null || info.isFixed()) {
            if (info == null) {
                info = new AccessResourceInfo();
            }
            info.setModel(namespace);
            info.setActionName(fun);
            info.setPath(ResourcePath.generatorPath(namespace, fun));
            info.setIsFunction(!FunctionSourceEnum.ACTION.equals(source));
            info.setIsFixed(true);
            AccessResourceInfoSession.setInfo(info);
        } else if (FunctionSourceEnum.ACTION.equals(source)) {
            if (!isSameAction(info, namespace, fun)) {
                info.addActionPath(namespace, fun);
            }
        }
        for (AccessResourceInfoPrepareApi prepareApi : BeanDefinitionUtils.getBeansOfTypeByOrdered(AccessResourceInfoPrepareApi.class)) {
            prepareApi.prepareAccessInfo(function, namespace, fun, source);
        }
    }

    private boolean isSameAction(AccessResourceInfo info, String model, String actionName) {
        ResourcePath lastPath = info.getLastPath();
        if (lastPath == null) {
            if (info.isActionPath()) {
                return model.equals(info.getModel()) && actionName.equals(info.getActionName());
            }
            return false;
        }
        return model.equals(lastPath.getModel()) && actionName.equals(lastPath.getName());
    }
}
