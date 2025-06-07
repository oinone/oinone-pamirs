package pro.shushi.pamirs.framework.connectors.data.util;

import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * 数据源映射帮助类帮助类
 *
 * @author Adamancy Zhang at 09:52 on 2024-11-05
 */
@Slf4j
public class DataConfigurationHelper {

    private static final HoldKeeper<PamirsMapperConfiguration> mapperConfigurationHolder = new HoldKeeper<>();
    private static final HoldKeeper<PamirsFrameworkDataConfiguration> dataConfigurationHolder = new HoldKeeper<>();

    private DataConfigurationHelper() {
        // reject create object
    }

    public static PamirsMapperConfiguration getMapperConfiguration() {
        return mapperConfigurationHolder.supply(() -> BeanDefinitionUtils.getBean(PamirsMapperConfiguration.class));
    }

    public static PamirsFrameworkDataConfiguration getDataConfiguration() {
        return dataConfigurationHolder.supply(() -> BeanDefinitionUtils.getBean(PamirsFrameworkDataConfiguration.class));
    }

    public static String getDsKey() {
        Object dsKey = PamirsSession.getDsKey();
        if (dsKey == null) {
            dsKey = getDataConfiguration().getDefaultDsKey();
            if (log.isWarnEnabled()) {
                log.warn("No dsKey specified, default dsKey will be automatically used. dsKey: {}", dsKey);
            }
        }
        return String.valueOf(dsKey);
    }

    public static String getDsKey(String model) {
        Object dsKey = PamirsSession.getDsKey();
        if (null == dsKey) {
            dsKey = getMapperConfiguration().getDataSourceRouteService().route(model);
        }
        if (null == dsKey) {
            dsKey = getDataConfiguration().getDefaultDsKey();
            if (log.isWarnEnabled()) {
                log.warn("No model dsKey specified, default dsKey will be automatically used. dsKey: {}", dsKey);
            }
        }
        return String.valueOf(dsKey);
    }
}
