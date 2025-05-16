package pro.shushi.pamirs.framework.connectors.data.api.service.implementation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.api.service.ModuleDsService;
import pro.shushi.pamirs.meta.common.spi.SPI;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 2020/6/8 12:23 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
@Component
public class DefaultModuleDsService implements ModuleDsService {

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Override
    public Map<String/*module*/, String/*dsKey*/> dsMap() {
        return pamirsFrameworkDataConfiguration.getDsMap();
    }

    @Override
    public Map<String/*model*/, String/*dsKey*/> modelDsMap() {
        return pamirsFrameworkDataConfiguration.getModelDsMap();
    }

}
