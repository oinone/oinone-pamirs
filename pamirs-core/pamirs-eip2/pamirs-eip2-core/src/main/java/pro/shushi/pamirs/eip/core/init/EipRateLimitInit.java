package pro.shushi.pamirs.eip.core.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.service.EipOpenRateLimitPolicyService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 开放接口流控初始化
 *
 * @author yeshenyue on 2025/4/22 16:42.
 */
@Component
public class EipRateLimitInit implements InstallDataInit, UpgradeDataInit {

    @Resource
    private EipOpenRateLimitPolicyService rateLimitPolicyService;

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        rateLimitPolicyService.init();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        rateLimitPolicyService.init();
        return true;
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
