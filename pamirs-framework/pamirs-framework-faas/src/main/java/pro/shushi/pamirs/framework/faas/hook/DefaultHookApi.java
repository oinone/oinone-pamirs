package pro.shushi.pamirs.framework.faas.hook;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.faas.FunEngine;
import pro.shushi.pamirs.framework.faas.configure.PamirsFrameworkHookConfiguration;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.hook.HookApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.fun.Hook;
import pro.shushi.pamirs.meta.enmu.HookTypeEnum;

import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 扩展点API默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SuppressWarnings("unused")
@Component
public class DefaultHookApi implements HookApi {

    @Resource
    private PamirsFrameworkHookConfiguration pamirsFrameworkHookConfiguration;

    @Override
    public void before(String namespace, String fun, Object... args) {
        run(HookTypeEnum.BEFORE, namespace, fun, args);
    }

    @Override
    public void after(String namespace, String fun, Object ret) {
        run(HookTypeEnum.AFTER, namespace, fun, ret);
    }

    private void run(HookTypeEnum type, String namespace, String fun, Object... args) {
        if (pamirsFrameworkHookConfiguration.isIgnoreAll()) {
            return;
        }
        Function function = PamirsSession.getContext().getFunctionAllowNull(namespace, fun);
        if (null == function) {
            return;
        }
        List<Hook> hooks = Objects.requireNonNull(PamirsSession.getContext())
                .getExecuteHooks(type, namespace, fun, function.getType(), pamirsFrameworkHookConfiguration.getExcludes());
        if (CollectionUtils.isEmpty(hooks)) {
            return;
        }
        hooks.sort(Comparator.comparing(Hook::getPriority));
        for (Hook hook : hooks) {
            String executeNamespace = hook.getExecuteNamespace();
            String executeFun = hook.getExecuteFun();
            if (!CollectionUtils.isEmpty(hook.getFunctionTypes()) && null != function.getType() && !hook.getFunctionTypes().retainAll(function.getType())) {
                continue;
            }
            Function hookFunction = PamirsSession.getContext().getFunction(executeNamespace, executeFun);
            Models.directive().run(() -> FunEngine.get().run(hookFunction, function, args));
        }
    }

}
