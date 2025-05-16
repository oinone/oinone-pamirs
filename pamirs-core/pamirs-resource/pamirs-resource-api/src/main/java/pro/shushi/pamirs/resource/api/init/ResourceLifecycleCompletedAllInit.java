package pro.shushi.pamirs.resource.api.init;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedAllInit;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-11-27 18:03
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class ResourceLifecycleCompletedAllInit implements LifecycleCompletedAllInit {

    @Override
    public void process(AppLifecycleCommand command, Map<String, ModuleDefinition> runModuleMap) {
        InitializationUtil.gc();
//        System.gc();
    }
}
