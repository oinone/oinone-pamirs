package pro.shushi.pamirs.framework.connectors.event.spi.extension;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.spi.NotifyTopicEditorApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认 Notify Topic 编辑实现
 *
 * @author Adamancy Zhang on 2021-05-13 09:22
 */
@Component
@Order
@SPI.Service
public class DefaultNotifyTopicEditor implements NotifyTopicEditorApi {

    @Override
    public String handlerTopic(String topic) {
        return topic;
    }
}
