package pro.shushi.pamirs.framework.connectors.event.spi.extension;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.common.PamirsEventProperties;
import pro.shushi.pamirs.framework.connectors.event.spi.SystemTopicEditorApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

/**
 * 默认租户Topic编辑实现（不修改topic）
 *
 * @author Adamancy Zhang at 12:09 on 2021-08-18
 */
@Component
@Order
@SPI.Service
public class DefaultSystemTopicEditor implements SystemTopicEditorApi {

    @Autowired
    private PamirsFrameworkSystemConfiguration systemConfiguration;

    @Autowired(required = false)
    private PamirsEventProperties eventProperties;

    @Override
    public String handlerTopic(String topic) {
        if (eventProperties == null) {
            return systemHandlerTopic(topic);
        }
        String topicPrefix = eventProperties.getTopicPrefix();
        if (StringUtils.isBlank(topicPrefix)) {
            return systemHandlerTopic(topic);
        }
        return topicPrefix + topic;
    }

    private String systemHandlerTopic(String topic) {
        String isolationKey = systemConfiguration.getIsolationKey();
        if (StringUtils.isBlank(isolationKey)) {
            return topic;
        }
        return isolationKey + topic;
    }
}
