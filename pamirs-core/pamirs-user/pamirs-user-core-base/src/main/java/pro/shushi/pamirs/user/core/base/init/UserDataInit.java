package pro.shushi.pamirs.user.core.base.init;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.user.api.service.UserDataInitService;

import java.util.List;

/**
 * @author shier
 * date  2022/6/2 下午5:18
 */
@Order(1000)
@Component
public class UserDataInit implements LifecycleCompletedInit {

    @Override
    public void process(AppLifecycleCommand command, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        List<UserDataInitService> extensions = Spider.getLoader(UserDataInitService.class).getExtensions();
        if (null != extensions) {
            for (UserDataInitService userDataInitService : extensions) {
                userDataInitService.invoke();
            }
        }
    }
}
