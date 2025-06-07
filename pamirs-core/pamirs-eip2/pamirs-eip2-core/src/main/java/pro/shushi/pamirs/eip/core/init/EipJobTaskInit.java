package pro.shushi.pamirs.eip.core.init;

import org.springframework.stereotype.Component;
import org.thymeleaf.util.MapUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.core.task.abs.EipAbstractScheduledJob;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Eip定时任务初始化
 * @author yeshenyue on 2025/4/10 19:10.
 */
@Component
public class EipJobTaskInit implements InstallDataInit, UpgradeDataInit {

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        initJob();
        return true;
    }

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        initJob();
        return true;
    }

    private void initJob() {
        Map<String, EipAbstractScheduledJob> map = BeanDefinitionUtils.getBeansOfType(EipAbstractScheduledJob.class);
        if (MapUtils.isEmpty(map)) {
            return;
        }
        for (String className : map.keySet()) {
            map.get(className).initTask();
        }
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(EipModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }
}
