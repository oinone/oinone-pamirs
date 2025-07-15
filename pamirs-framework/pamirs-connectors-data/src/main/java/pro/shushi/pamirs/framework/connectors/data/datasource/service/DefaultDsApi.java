package pro.shushi.pamirs.framework.connectors.data.datasource.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.api.service.DataSourceRouteService;
import pro.shushi.pamirs.framework.connectors.data.api.service.ModuleDsService;
import pro.shushi.pamirs.framework.connectors.data.configure.datasource.DataSourceConfiguration;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * 默认ds接口实现
 * <p>
 * 2020/11/10 3:43 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(99)
@SPI.Service
@Component
public class DefaultDsApi implements DsApi {

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Resource
    private PamirsFrameworkSystemConfiguration pamirsFrameworkSystemConfiguration;

    @Resource
    private DataSourceConfiguration dataSourceConfiguration;

    @Resource
    private ModuleDsService moduleDsService;

    @Override
    public boolean existDs(String model) {
        Object dsKey = Spider.getDefaultExtension(DataSourceRouteService.class).route(model);
        if (null == dsKey) {
            return true;
        }
        String ds = (String) dsKey;
        return dataSourceConfiguration.containsKey(ds);
    }

    @Override
    public String systemDsKey() {
        return pamirsFrameworkSystemConfiguration.getSystemDsKey();
    }

    @Override
    public String originSystemDsKey() {
        return pamirsFrameworkSystemConfiguration.getOriginSystemDsKey();
    }

    @Override
    public String baseDsKey(String model) {
        return Optional.ofNullable(fetchModuleDsMap())
                .map(v -> v.get(ModuleConstants.MODULE_BASE))
                .map(v -> DataPrefixManager.dsPrefix(ModuleConstants.MODULE_BASE, model, v))
//                .orElse(defaultDsKey());
                .orElse(pamirsFrameworkSystemConfiguration.getOriginSystemDsKey());
    }

    @Override
    public String defaultDsKey() {
        return pamirsFrameworkDataConfiguration.getDefaultDsKey();
    }

    @Override
    public String originDefaultDsKey() {
        return pamirsFrameworkDataConfiguration.getOriginDefaultDsKey();
    }

    @Override
    public Map<String, String> fetchModuleDsMap() {
        return moduleDsService.dsMap();
    }

    @Override
    public Map<String, String> fetchModelDsMap() {
        return moduleDsService.modelDsMap();
    }

}
