package pro.shushi.pamirs.framework.connectors.event.spi.extension;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.common.PamirsEventProperties;
import pro.shushi.pamirs.framework.connectors.event.spi.SystemConsumerGroupEditorApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

/**
 * 默认租户ConsumerGroup编辑实现（不修改topic）
 *
 * @author Adamancy Zhang at 11:54 on 2021-08-26
 */
@Component
@Order
@SPI.Service
public class DefaultSystemConsumerGroupEditor implements SystemConsumerGroupEditorApi {

    @Autowired
    private PamirsFrameworkSystemConfiguration systemConfiguration;

    @Autowired(required = false)
    private PamirsEventProperties pamirsEventProperties;

    @Override
    public String handlerConsumerGroup(String group) {
        String finalGroup = group;
        if (pamirsEventProperties != null && StringUtils.isNotBlank(pamirsEventProperties.getTopicPrefix())) {
            finalGroup = pamirsEventProperties.getTopicPrefix() + finalGroup;
        }
        return systemHandlerConsumerGroup(finalGroup);
    }

    private String systemHandlerConsumerGroup(String group) {
        String isolationKey = systemConfiguration.getIsolationKey();
        if (StringUtils.isBlank(isolationKey)) {
            return group;
        }
        return isolationKey + group;
    }
}
