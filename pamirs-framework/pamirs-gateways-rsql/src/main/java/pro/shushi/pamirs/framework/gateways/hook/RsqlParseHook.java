package pro.shushi.pamirs.framework.gateways.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlParseHelper;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * 解释Rsql
 *
 * @author shier
 * date 2020/4/20
 */
@Base
@Slf4j
@Component
public class RsqlParseHook implements HookBefore {

    @Override
    @Hook(priority = 40)
    public Object run(Function function, Object... args) {
        if (null != args && args.length > 0) {
            int index = 0;
            while (index < args.length && null != args[index]) {
                if (args[index] instanceof AbstractWrapper) {
                    String namespace = function.getNamespace();
                    if (PamirsSession.getContext().getSimpleModelConfig(namespace) != null) {
                        parse((AbstractWrapper<?, ?, ?>) args[index], namespace);
                    }
                    break;
                }
                index++;
            }
        }
        return function;
    }

    public void parse(AbstractWrapper<?, ?, ?> wrapper, String model) {
        RsqlParseHelper.parseQueryWrapper(wrapper, model);
    }
}
