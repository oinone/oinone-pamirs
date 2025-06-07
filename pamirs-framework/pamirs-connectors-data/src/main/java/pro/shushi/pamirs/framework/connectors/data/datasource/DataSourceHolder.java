package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DataSourceApi;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源持有者
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 2:02 上午
 */
@Component
public class DataSourceHolder implements DataSourceApi {

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();

    protected void put(String ds, DataSource dataSource) {
        dataSourceMap.put(getLookupKeyByDsKey(ds), dataSource);
    }

    @Override
    public DataSource get(Object ds) {
        String dsKey;
        String defaultDsKey = pamirsFrameworkDataConfiguration.getDefaultDsKey();
        if (null == ds) {
            dsKey = defaultDsKey;
        } else {
            dsKey = String.valueOf(ds);
        }
        String lookupKey = getLookupKeyByDsKey(dsKey);
        DataSource dataSource = (DataSource) dataSourceMap.get(lookupKey);
        if (null == dataSource && StringUtils.isNotBlank(defaultDsKey)) {
            defaultDsKey = getLookupKeyByDsKey(defaultDsKey);
            return (DataSource) dataSourceMap.get(defaultDsKey);
        }
        return dataSource;
    }

    protected Map<Object, Object> getDataSourceMap() {
        return this.dataSourceMap;
    }

    public static String getLookupKeyByDsKey(Object dsKey) {
        if (null == dsKey) {
            return null;
        }
        return dsKey + CharacterConstants.SEPARATOR_UNDERLINE + DataSource.class.getSimpleName();
    }

}
