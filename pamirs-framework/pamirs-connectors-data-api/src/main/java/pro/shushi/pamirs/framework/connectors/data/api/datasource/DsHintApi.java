package pro.shushi.pamirs.framework.connectors.data.api.datasource;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.api.service.DataSourceRouteService;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.DynamicDsKeyComputer;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.ParserUtil;

import java.util.Map;
import java.util.Optional;

/**
 * 强制指定数据源
 * <p>
 * 2020/7/9 10:42 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DsHintApi implements AutoCloseable {

    public static DsHintApi use(Object dsKey) {
        return new DsHintApi(dsKey);
    }

    public static DsHintApi model(String model) {
        Object dsKey = null;
        if (StringUtils.isNotBlank(model)) {
            dsKey = Spider.getDefaultExtension(DataSourceRouteService.class).route(model);
        }
        return new DsHintApi(dsKey);
    }

    public DsHintApi(Object dsKey) {
        PamirsSession.pushDsKey(expression(dsKey));
    }

    @Override
    public void close() {
        PamirsSession.clearDsKey();
    }

    public static String expression(Object dsKey) {
        if (null == dsKey) {
            return null;
        }
        String dsKeyString = String.valueOf(dsKey);
        Map<String, Object> context = Optional.ofNullable(CommonApiFactory.getApi(PamirsMapperConfigurationProxy.class))
                .map(PamirsMapperConfigurationProxy::fetchDynamicDsKeyComputer).map(DynamicDsKeyComputer::context).orElse(null);
        return ParserUtil.replaceWithMap(dsKeyString, context);
    }

}
