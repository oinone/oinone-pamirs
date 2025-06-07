package pro.shushi.pamirs.framework.connectors.data.datasource.event;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.constant.SystemBeanConstants;
import pro.shushi.pamirs.framework.connectors.data.util.ConfigureUtils;

import static pro.shushi.pamirs.framework.common.constants.ConfigureConstants.DATASOURCE_CONFIG_PREFIX;

/**
 * 数据源配置处理类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 3:20 上午
 */
@Component
public class DataSourceRefreshEventPreHandler implements RefreshEventHandler {

    @Override
    public boolean needHandle(String changeKey) {
        return changeKey.startsWith(DATASOURCE_CONFIG_PREFIX);
    }

    @Override
    public void handle(RefreshContext context, String changeKey) {
        String dsKey = ConfigureUtils.substringForNamespace(changeKey, DATASOURCE_CONFIG_PREFIX);
        if (StringUtils.isNotBlank(dsKey)) {
            context.getRefreshConfSet().add(SystemBeanConstants.DATA_SOURCE_CONFIGURATION);
            context.getRefreshDsSet().add(dsKey);
        }
    }

}
