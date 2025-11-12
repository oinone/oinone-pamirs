package pro.shushi.pamirs.boot.base.api.module;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

import java.util.Set;

/**
 * base module配置类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 6:33 下午
 */
@Component
@Base
@Boot
@Module(
        name = ModuleConstants.MODULE_BASE_NAME,
        displayName = "基础模块",
        version = "1.0.0",
        dependencies = {},
        exclusions = {}
)
@Module.module(ModuleConstants.MODULE_BASE)
@Module.Advanced(application = false, web = false, selfBuilt = true, core = true, author = "pamirs", contributors = "huidao", maintainer = "huidao")
public class BaseModule implements PamirsModule, InitializingBean {

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.meta.api.dto.common",
                "pro.shushi.pamirs.meta.api.dto.condition",
                "pro.shushi.pamirs.meta.api.dto.wrapper",
                "pro.shushi.pamirs.meta.api.dto.crud",
                "pro.shushi.pamirs.meta.domain",
                "pro.shushi.pamirs.meta.enmu",
                "pro.shushi.pamirs.meta.enumclass",
                "pro.shushi.pamirs.meta.base",
                "pro.shushi.pamirs.framework.compute.system.check",
                "pro.shushi.pamirs.framework.configure.core.data.module.base",
                "pro.shushi.pamirs.framework.orm",
                "pro.shushi.pamirs.framework.faas.hook",
                "pro.shushi.pamirs.framework.faas.fun.builtin",
                "pro.shushi.pamirs.framework.faas.expression.service",
                "pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic",
                "pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical",
                "pro.shushi.pamirs.framework.gateways.hook",
                "pro.shushi.pamirs.boot.base",
                "pro.shushi.pamirs.boot.web.loader",
                "pro.shushi.pamirs.boot.web.action",
                "pro.shushi.pamirs.boot.modules",
                "pro.shushi.pamirs.sid",
                "pro.shushi.pamirs.sequence",
                "pro.shushi.pamirs.metadata.manager"
        };
    }

    @Autowired
    private BootConfiguration bootConfiguration;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 自启动
        Set<String> modules = bootConfiguration.getModules();
        modules.add(ModuleConstants.MODULE_BASE);
    }
}
