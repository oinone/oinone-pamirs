package pro.shushi.pamirs.framework.connectors.event.spi.extension;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.event.spi.ConsumerGroupEditorApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认消费者组编辑实现
 *
 * @author Adamancy Zhang at 11:48 on 2021-08-26
 */
@Component
@Order
@SPI.Service
public class DefaultConsumerGroupEditor implements ConsumerGroupEditorApi {

    @Override
    public String handlerConsumerGroup(String group) {
        return group;
    }
}
