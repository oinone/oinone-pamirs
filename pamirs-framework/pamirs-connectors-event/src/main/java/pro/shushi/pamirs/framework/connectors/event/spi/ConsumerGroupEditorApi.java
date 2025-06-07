package pro.shushi.pamirs.framework.connectors.event.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 消费者组编辑API
 *
 * @author Adamancy Zhang at 11:46 on 2021-08-26
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ConsumerGroupEditorApi {

    /**
     * 生成消费者组
     *
     * @param group 旧消费者组
     * @return 新消费者组
     */
    String handlerConsumerGroup(String group);
}
